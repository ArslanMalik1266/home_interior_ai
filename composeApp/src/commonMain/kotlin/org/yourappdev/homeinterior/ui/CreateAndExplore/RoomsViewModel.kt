package org.yourappdev.homeinterior.ui.CreateAndExplore

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.yourappdev.homeinterior.data.local.entities.DraftEntity
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.data.mapper.toUi
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomRequest
import org.yourappdev.homeinterior.domain.repo.DraftsRepository
import org.yourappdev.homeinterior.domain.repo.RecentGeneratedRepository
import org.yourappdev.homeinterior.domain.repo.RoomsRepository
import org.yourappdev.homeinterior.domain.usecase.AddCreditsUseCase
import org.yourappdev.homeinterior.domain.usecase.FetchGeneratedRoomUseCase
import org.yourappdev.homeinterior.domain.usecase.GenerateRoomUseCase
import org.yourappdev.homeinterior.domain.usecase.SpendCreditsUseCase
import org.yourappdev.homeinterior.domain.usecase.SpendCreditsUseCaseGuest
import org.yourappdev.homeinterior.domain.usecase.StartImageTrackingUseCase
import org.yourappdev.homeinterior.ui.Generate.UiScreens.ColorPalette
import org.yourappdev.homeinterior.ui.Generate.UiScreens.InteriorStyle
import org.yourappdev.homeinterior.ui.authentication.AuthViewModel
import org.yourappdev.homeinterior.ui.authentication.register.RegisterEvent
import org.yourappdev.homeinterior.ui.common.base.CommonUiEvent
import org.yourappdev.homeinterior.ui.common.base.CommonUiEvent.ShowError
import org.yourappdev.homeinterior.utils.NotificationManager
import org.yourappdev.homeinterior.utils.downloadAndCacheImage
import org.yourappdev.homeinterior.utils.executeApiCall
import org.yourappdev.homeinterior.utils.getDeviceId
import org.yourappdev.homeinterior.utils.readLocalFile
import org.yourappdev.homeinterior.utils.toBase64
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RoomsViewModel(
    val roomsRepository: RoomsRepository,
    private val addCreditsUseCase: AddCreditsUseCase,
    private val authViewModel: AuthViewModel,
    private val draftsRepository: DraftsRepository,
    private val recentGeneratedRepository: RecentGeneratedRepository,
    private val spendCreditsUseCase: SpendCreditsUseCase,
    private val generateRoomUseCase: GenerateRoomUseCase,
    private val fetchGeneratedRoomUseCase: FetchGeneratedRoomUseCase,
    private val httpClient: io.ktor.client.HttpClient,
    private val spendCreditsUseCaseGuest: SpendCreditsUseCaseGuest,
    private val startImageTrackingUseCase: StartImageTrackingUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(RoomUiState())
    val state: StateFlow<RoomUiState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CommonUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
    private val _selectedGeneratedImage = MutableStateFlow<String?>(null)
    val selectedGeneratedImage: StateFlow<String?> = _selectedGeneratedImage.asStateFlow()
    val draftImages: StateFlow<List<DraftEntity>> = draftsRepository.getAllDrafts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    private var currentDraftId: Long? = null
    fun selectDraftImage(draft: DraftEntity) {
        currentDraftId = draft.id

        onRoomEvent(
            RoomEvent.SetImageBytes(
                bytes = draft.userImageBytes ?: byteArrayOf(),
                fileName = "draft_${draft.id}.jpg"
            )
        )
        _state.update {
            it.copy(
                selectedRoomType = draft.roomType,
                selectedStyleName = draft.style,
                selectedPaletteId = draft.paletteId,
                currentPage = draft.currentPage,
                selectedImage = "draft_picked"
            )
        }
    }
    val dbGeneratedImages: StateFlow<List<RecentGeneratedEntity>> =
        recentGeneratedRepository.getRecentGenerated()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    @OptIn(ExperimentalTime::class)
    fun saveOrUpdateDraft() {
        val currentState = _state.value
        val currentImage = currentState.selectedImageBytes ?: return

        // Room Entity object banayein
        val newDraft = DraftEntity(
            id = currentDraftId ?: 0L, // Agar ID hai to update hoga, 0 hai to naya banega
            userImageBytes = currentImage,
            roomType = currentState.selectedRoomType ?: "Living Room",
            style = currentState.selectedStyleName ?: "Modern",
            paletteId = currentState.selectedPaletteId ?: 0,
            currentPage = currentState.currentPage,
            createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
        )

        viewModelScope.launch {
            draftsRepository.saveDraft(newDraft)
            currentDraftId = null
            resetGenerationState()
        }
    }

    fun selectDraftForEditing(draft: DraftEntity) {
        currentDraftId = draft.id

        _state.update {
            it.copy(
                selectedImageBytes = draft.userImageBytes,
                selectedRoomType = draft.roomType,
                selectedStyleName = draft.style,
                selectedPaletteId = draft.paletteId,
                currentPage = draft.currentPage,
                selectedImage = "draft_picked"
            )
        }
    }

    fun onGeneratedImageClick(imageUrl: String) {
        _selectedGeneratedImage.value = imageUrl
    }

    fun resetSelectedGeneratedImage() {
        _selectedGeneratedImage.value = null
    }

    init {
        getRooms()
        viewModelScope.launch {
            println("🟢 FETCH_FLOW: Starting to observe database...")
            dbGeneratedImages.collect { images ->
                println("🟢 FETCH_FLOW: Database changed! Total records = ${images.size}")

                if (images.isEmpty()) {
                    println("🟢 FETCH_FLOW: ⚠️ Database is EMPTY")
                } else {
                    images.forEachIndexed { index, entity ->
                        println("🟢 FETCH_FLOW: [$index] -----------------")
                        println("🟢 FETCH_FLOW:   - ID = ${entity.id}")
                        println("🟢 FETCH_FLOW:   - URL = ${entity.imageUrl}")
                        println("🟢 FETCH_FLOW:   - Created at = ${entity.createdAt}")
                    }
                }
            }
        }
    }

    fun resetGenerationState() {
        _state.update {
            it.copy(
                selectedRoomType = null,
                selectedStyleName = null,
                selectedPaletteId = null,
                currentPage = 0, // <--- YE LINE ADD KAREIN
                errorMessage = null,
                isGenerating = false,
                generatedImages = emptyList()
            )
        }
        currentDraftId = null
    }

    @OptIn(ExperimentalTime::class, ExperimentalEncodingApi::class)
    fun onRoomEvent(event: RoomEvent) {
        when (event) {

            // Filtering and search events (keep as-is)
            is RoomEvent.OnSearchQueryChange -> {
                _state.value = _state.value.copy(searchQuery = event.query)
                applyFiltersAndSearch()
            }

            is RoomEvent.SetImageBytes -> {
                _state.update {
                    it.copy(
                        selectedImageBytes = event.bytes,
                        selectedFileName = event.fileName,
                        selectedImage = "image_picked"
                    )
                }
            }


            is RoomEvent.OnApplyFilters -> {
                val tempFilter = _state.value.tempFilterState
                val count = calculateFilterCount(tempFilter)
                _state.value = _state.value.copy(
                    filterState = tempFilter,
                    filterCount = count,
                    tempFilterCount = count,
                    showFilterSheet = false
                )
                applyFiltersAndSearch()
            }

            RoomEvent.OnFilterClick -> {
                _state.value = _state.value.copy(
                    showFilterSheet = true,
                    tempFilterState = _state.value.filterState,
                    tempFilterCount = _state.value.filterCount
                )
            }

            RoomEvent.OnResetLoading -> {
                _state.update { it.copy(isLoading = false) }
            }

            RoomEvent.OnDismissFilterSheet -> {
                _state.value = _state.value.copy(
                    showFilterSheet = false,
                    tempFilterCount = _state.value.filterCount
                )
            }

            RoomEvent.OnClearFilters -> {
                _state.value = _state.value.copy(
                    tempFilterState = FilterState(),
                    tempFilterCount = 0
                )
            }

            is RoomEvent.OnTempFilterChange -> {
                val newCount = calculateFilterCount(event.filterState)
                _state.value = _state.value.copy(
                    tempFilterState = event.filterState,
                    tempFilterCount = newCount
                )
            }

            is RoomEvent.OnToggleFilterSection -> {
                _state.value = when (event.section) {
                    FilterSection.ROOM_TYPE -> _state.value.copy(
                        expandedRoomType = !_state.value.expandedRoomType
                    )

                    FilterSection.STYLE -> _state.value.copy(
                        expandedStyle = !_state.value.expandedStyle
                    )

                    FilterSection.COLOR -> _state.value.copy(
                        expandedColor = !_state.value.expandedColor
                    )

                    FilterSection.FORMAT -> _state.value.copy(
                        expandedFormat = !_state.value.expandedFormat
                    )

                    FilterSection.PRICE -> _state.value.copy(
                        expandedPrice = !_state.value.expandedPrice
                    )
                }
            }
            is RoomEvent.SetImage -> {
                _state.value = _state.value.copy(
                    selectedImage = event.imageDetails.uri
                )
                println("DEBUG_VM: SelectedImage URI = ${event.imageDetails.uri}")
            }

            // Pagination events
            is RoomEvent.OnPageChange -> {
                _state.value = _state.value.copy(currentPage = event.page)
            }

            RoomEvent.OnNextPage -> {
                val currentPage = _state.value.currentPage
                if (currentPage < _state.value.pageCount - 1) {
                    _state.value = _state.value.copy(currentPage = currentPage + 1)
                }
            }

            RoomEvent.OnPreviousPage -> {
                val currentPage = _state.value.currentPage
                if (currentPage > 0) {
                    _state.value = _state.value.copy(currentPage = currentPage - 1)
                }
            }

            // Room type / style / palette selection events
            is RoomEvent.OnRoomTypeSelected -> {
                _state.value = _state.value.copy(selectedRoomType = event.roomType)
            }

            is RoomEvent.OnRoomSearchQueryChange -> {
                _state.value = _state.value.copy(roomSearchQuery = event.query)
            }

            is RoomEvent.OnRoomSearchExpandedChange -> {
                _state.value = _state.value.copy(isRoomSearchExpanded = event.isExpanded)
            }

            is RoomEvent.OnStyleSelected -> {
                val styleName = _state.value.availableStyles
                    .firstOrNull { it.id == event.styleId }
                    ?.name ?: "Unknown"
                _state.value = _state.value.copy(selectedStyleName = styleName)
            }

            is RoomEvent.OnStyleSearchQueryChange -> {
                _state.value = _state.value.copy(styleSearchQuery = event.query)
            }

            is RoomEvent.OnStyleSearchExpandedChange -> {
                _state.value = _state.value.copy(isStyleSearchExpanded = event.isExpanded)
            }

            is RoomEvent.OnPaletteSelected -> {
                _state.value = _state.value.copy(selectedPaletteId = event.paletteId)
            }

            is RoomEvent.OnGenerateClick -> {
                println("🔴 GEN_START: Button clicked!")
                println("🔴 GEN_START: email = '${authViewModel.state.value.email}'")
                println("🔴 GEN_START: totalCredits = ${authViewModel.state.value.totalCredits}")
                println("🔴 GEN_START: freeCredits = ${authViewModel.state.value.freeCredits}")
                println("🔴 GEN_START: imageBytes = ${event.imageBytes.size}")
                println("🔴 GEN_CHECK: About to check credits guard...")
                val currentCredits = if (authViewModel.state.value.email.isNullOrBlank()) {
                    // Guest — guestSession se lo
                    authViewModel.guestSession.value?.totalCredits ?: 0
                } else {
                    // Logged in
                    authViewModel.state.value.totalCredits
                }

                println("🔴 GEN_CHECK: currentCredits = $currentCredits")

                if (currentCredits <= 0) {
                    println("🔴 GEN_CHECK: ❌ NO CREDITS — returning!")
                    viewModelScope.launch { _uiEvent.emit(ShowError("Not enough credits")) }
                    return
                }

                _state.update {
                    it.copy(isGenerating = true, errorMessage = null)
                }
                println("🔴 GEN_CHECK: Passed credits guard, launching coroutine...")


                viewModelScope.launch {
                    try {
                        val email = authViewModel.state.value.email ?: ""
                        val deviceId = getDeviceId()
                        println("🔴 GEN_CREDIT: email blank? = ${email.isBlank()}")
                        println("🔴 GEN_CREDIT: deviceId = $deviceId")

                        val creditResult = if (email.isBlank()) {
                            println("🔴 GEN_CREDIT: Using GUEST use case...")

                            // Guest user
                            println("DEBUG_GENERATE: Guest user — using SpendCreditsUseCaseGuest")
                            spendCreditsUseCaseGuest(
                                deviceId = deviceId,
                                amount = 1
                            )
                        } else {
                            println("🔴 GEN_CREDIT: Using LOGGED IN use case...")

                            // Logged in user
                            println("DEBUG_GENERATE: Logged in user — using SpendCreditsUseCase")
                            spendCreditsUseCase(
                                userEmail = email,
                                deviceId = deviceId,
                                amount = 1
                            )
                        }
                        println("🔴 GEN_CREDIT: result = ${creditResult.isSuccess}")


                        if (creditResult.isFailure) {
                            println("🔴 GEN_CREDIT: FAILED! reason = ${creditResult.exceptionOrNull()?.message}")

                            val error = creditResult.exceptionOrNull()?.message
                            println("🔴 SERVER_REJECTED: $error")
                            _state.update { it.copy(
                                isGenerating = false,
                                isFetchingImages = false, // Isse loading properly rukegi
                                errorMessage = "Not enough credits"
                            )}

                            _uiEvent.emit(ShowError("Not enough credits"))
                            return@launch
                        }
                        println("🔴 GEN_CREDIT: Credits spent! Moving to generation...")

                        if (!email.isNullOrBlank()) {
                            authViewModel.fetchUserDetails()
                        }
                        val base64Image = "data:image/jpeg;base64,${event.imageBytes.toBase64()}"
                        val prompt = buildPromptFromState(_state.value)
                        val request = GenerateRoomRequest(initImage = base64Image, prompt = prompt)

                        // ✅ 3 parallel calls
                        val job1 = async { generateRoomUseCase(request) }
                        val job2 = async { generateRoomUseCase(request) }
                        val job3 = async { generateRoomUseCase(request) }
                        val results = awaitAll(job1, job2, job3)

                        // ✅ Pehle 3 empty boxes dikhao
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                isFetchingImages = true,
                                generatedCount = 3,
                                generatedImages = emptyList(),
                                generatedImagesEntity = emptyList()
                            )
                        }
                        val allGeneratedUrls = mutableListOf<String>()
                        val allLocalPaths = mutableListOf<String>()

                        // ✅ 3no ko alag alag fetch karo
                        results.forEachIndexed { index, result ->
                            if (result is ResultState.Success) {
                                val response = result.data
                                if (response.isProcessing && response.fetchUrl != null) {
                                    launch {
                                        kotlinx.coroutines.delay((response.eta ?: 30) * 1000L)
                                        var fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                                        var retries = 0

                                        while (retries < 20) {
                                            if (fetchResult is ResultState.Success) {
                                                val data = fetchResult.data
                                                if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                                                    val imageUrl = data.availableImages.first()
                                                    val localPath = downloadAndCacheImage(
                                                        url = imageUrl,
                                                        fileName = "interior_${index}_${kotlin.time.Clock.System.now().toEpochMilliseconds()}.jpg"
                                                    )

                                                    recentGeneratedRepository.saveGenerated(
                                                        RecentGeneratedEntity(
                                                            imageUrl = imageUrl,
                                                            localPath = localPath
                                                        )
                                                    )
                                                    if (currentDraftId != null) {
                                                        draftsRepository.deleteDraftById(currentDraftId!!)
                                                        currentDraftId = null
                                                    }
                                                    // ✅ Har image aate hi apne box mein show karo
                                                    _state.update { state ->
                                                        val newImages = state.generatedImages + imageUrl
                                                        val newEntities = state.generatedImagesEntity + RecentGeneratedEntity(
                                                            imageUrl = imageUrl,
                                                            localPath = localPath
                                                        )
                                                        val allDone = newImages.size >= 3
                                                        if (allDone) {
                                                            NotificationManager.notifyIfBackground()
                                                            println("🔔 NOTIFICATION: All 3 images done!")
                                                        }
                                                        state.copy(
                                                            generatedImages = newImages,
                                                            generatedImagesEntity = newEntities,
                                                            isFetchingImages = !allDone
                                                        )
                                                    }

                                                    break
                                                }
                                            }
                                            retries++
                                            kotlinx.coroutines.delay(10_000L)
                                            try {
                                                fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                                            } catch (e: Exception) {
                                                println("🔄 Network error, retry $retries: ${e.message}")
                                                // Sirf wait karo aur retry karo
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } catch (e: Exception) {
                        _state.update { it.copy(isGenerating = false, errorMessage = e.message) }
                    }
                }
            }
            is RoomEvent.OnGenerationComplete -> {
                _state.update {
                    it.copy(
                        selectedImageBytes = null,
                        selectedFileName = null,
                        selectedImage = null,
                        generatedImages = emptyList(),
                        generatedImagesEntity = emptyList(),
                        isGenerating = false,
                        selectedRoomType = null,
                        isFetchingImages = false,
                        generatedCount = 0,
                        selectedStyleName = null,
                        selectedPaletteId = null,
                        currentPage = 0
                    )
                }
            }
            is RoomEvent.ShowSelectedBundle -> {
                _state.update {
                    it.copy(
                        generatedImagesEntity   = event.bundle,
                        isGenerating = false
                    )
                }
            }

            else -> {}
        }
    }
    private fun applyFiltersAndSearch() {
        val state = _state.value
        var filtered = state.allRooms

        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter { room ->
                room.roomType.contains(
                    state.searchQuery,
                    ignoreCase = true
                )
            }
        }

        if (state.filterState.selectedRoomTypes.isNotEmpty() && !state.filterState.selectedRoomTypes.contains(
                "All"
            )
        ) {
            filtered =
                filtered.filter { room -> state.filterState.selectedRoomTypes.contains(room.roomType) }
        }

        if (state.filterState.selectedStyles.isNotEmpty() && !state.filterState.selectedStyles.contains(
                "All"
            )
        ) {
            filtered = filtered.filter { room ->
                state.filterState.selectedStyles.any { style ->
                    room.roomStyle.contains(
                        style,
                        ignoreCase = true
                    )
                }
            }
        }

        if (state.filterState.selectedColors.isNotEmpty()) {
            filtered = filtered.filter { room ->
                state.filterState.selectedColors.contains(room.id)
            }
        }

        _state.value = _state.value.copy(filteredRooms = filtered)
    }
    private fun calculateFilterCount(filterState: FilterState): Int {
        var count = 0
        if (filterState.selectedRoomTypes.isNotEmpty() && !filterState.selectedRoomTypes.contains("All")) count++
        if (filterState.selectedStyles.isNotEmpty() && !filterState.selectedStyles.contains("All")) count++
        if (filterState.selectedColors.isNotEmpty()) count++
        if (filterState.selectedFormats.isNotEmpty() && !filterState.selectedFormats.contains("All")) count++
        if (filterState.selectedPrices.isNotEmpty()) count++
        return count
    }
    private fun extractDynamicFilters(rooms: List<org.yourappdev.homeinterior.domain.model.RoomUi>) {
        val roomTypes = rooms.map { it.roomType }.filter { it.isNotBlank() }.distinct()

        val styles = rooms.map { data ->
            InteriorStyle(name = data.roomStyle, imageUrl = data.imageUrl, id = data.id)
        }.distinct()

        val colorPalettes =
            rooms.map { room -> ColorPalette(colors = room.colors, id = room.id) }.distinct()
        val stylesString = styles.map { it.name }.distinct()

        _state.value = _state.value.copy(
            availableRoomTypes = roomTypes,
            availableStyles = styles,
            availableStylesString = stylesString,
            availableColors = colorPalettes,
            selectedPaletteId = _state.value.selectedPaletteId ?: colorPalettes.firstOrNull()?.id
        )
    }
    fun getRooms() {
        println("DEBUG_VM: 1. getRooms() called") // Check if called
        viewModelScope.launch {
            executeApiCall(
                updateState = { result ->
                    _state.value = _state.value.copy(getRoomsResponse = result)
                },
                apiCall = {
                    println("DEBUG_VM: 2. Launching API Call...")
                    roomsRepository.getRoomsList()
                },
                onSuccess = { response ->
                    println("DEBUG_VM: 3. Success! Rooms Count: ${response.rooms.size}")
                    if (response.success) {
                        val finalList = response.rooms.map { it.toUi() }
                        val trending = finalList.filter { it.isTrending == 1 }
                        _state.value = _state.value.copy(
                            trendingRooms = trending,
                            allRooms = finalList,
                            filteredRooms = finalList,
                            isLoading = false
                        )
                        extractDynamicFilters(finalList)
                    } else {
                        println("DEBUG_VM: 4. API Success was False.")
                        _uiEvent.emit(ShowError("Something went wrong"))
                    }
                },
                onError = { errorMessage ->
                    println("DEBUG_VM: 5. API Error: $errorMessage")
                    viewModelScope.launch { _uiEvent.emit(ShowError(errorMessage)) }
                }
            )
        }
    }

    private fun buildPromptFromState(state: RoomUiState): String {
        val roomType = state.selectedRoomType?.ifBlank { "living room" } ?: "living room"
        val style = state.selectedStyleName?.ifBlank { "modern" } ?: "modern"

        // 1. Pehle selected palette dhoondein
        val selectedPalette = state.availableColors.firstOrNull { it.id == state.selectedPaletteId }

        println("🎨 PALETTE: selectedPaletteId = ${state.selectedPaletteId}")
        println("🎨 PALETTE: selectedPalette = $selectedPalette")
        println("🎨 PALETTE: availableColors count = ${state.availableColors.size}")
        // 2. Colors ko transform karein (Color object -> "FFFFFF")
        val cleanHexColors = selectedPalette?.colors?.map { colorValue ->
            when (colorValue) {
                is Color -> colorValue.toRawHex() // Agar Compose Color hai
                is String -> cleanColorString(colorValue) // Agar String hai
                else -> "FFFFFF"
            }
        } ?: listOf("neutral tones")

        val colorPaletteString = cleanHexColors.joinToString(", ")

        return """
      Photorealistic architectural photography of a $style $roomType, 
        interior design with $colorPaletteString color scheme. 
        High-end furniture, premium textures like velvet and polished wood, 
        soft cinematic sunlight through windows, 8k resolution, 
        highly detailed materials, sharp focus, masterpiece, 
        vibrant yet professional atmosphere, depth of field, no texts on photo, and Redecorate walls, maintain architecture.
    """.trimIndent()
    }

    // Helper to clean existing strings
    private fun cleanColorString(rawColor: String): String {
        return if (rawColor.contains("Color")) {
            // Agar galti se "Color(1.0...)" string ban chuka hai, toh usse handle karein
            "FFFFFF"
        } else {
            rawColor.replace("#", "").trim()
        }
    }

    // Compose Color to "FFFFFF"
    fun Color.toRawHex(): String {
        val r = (this.red * 255).toInt().coerceIn(0, 255)
        val g = (this.green * 255).toInt().coerceIn(0, 255)
        val b = (this.blue * 255).toInt().coerceIn(0, 255)

        // Har component ko 2-digit hex string mein badlein aur join karein
        return listOf(r, g, b).joinToString("") {
            it.toString(16).padStart(2, '0').uppercase()
        }
    }

    fun onSubscriptionEvent(event: RoomEvent) {
        when (event) {
            is RoomEvent.OnPurchasePlan -> {
                // Price to Credits Mapping
                val amount = when (event.price) {
                    "$9.99" -> 500
                    "$18.99" -> 1100
                    "$28.99" -> 2300
                    else -> 0
                }

                val email = authViewModel.state.value.email ?: ""

                println("DEBUG_PURCHASE: Event triggered for price: ${event.price}")
                println("DEBUG_PURCHASE: Mapped amount: $amount")
                println("DEBUG_PURCHASE: User email: '$email'")

                if (email.isBlank()) {
                    _state.update { it.copy(purchaseError = "User not logged in") }
                    return
                }

                _state.update { it.copy(isPurchasing = true, purchaseError = null) }

                viewModelScope.launch {
                    val result = addCreditsUseCase(email, amount)

                    result.onSuccess { response ->
                        _state.update {
                            it.copy(
                                isPurchasing = false,
                                purchaseSuccess = "Credits added: ${response.purchasedCredits}"
                            )
                        }
                        authViewModel.onAuthEvent(RegisterEvent.FetchUserDetails)
                    }.onFailure { error ->
                        println("DEBUG_PURCHASE: Failure! Error: ${error.message}")
                        _state.update {
                            it.copy(
                                isPurchasing = false,
                                purchaseError = error.message ?: "Transaction failed"
                            )
                        }
                    }
                }
            }

            RoomEvent.ClearPurchaseState -> {
                _state.update { it.copy(purchaseSuccess = null, purchaseError = null) }
            }

            else -> onRoomEvent(event) // Purane events ko bhej dein
        }
    }
    // RoomsViewModel.kt
    fun deleteRecentImage(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            println("DEBUG_VM: Attempting to delete image with ID: $id")
            try {
                recentGeneratedRepository.deleteGeneratedById(id)
                println("🗑️ DELETE: ID $id removed from DB")
                _uiEvent.emit(CommonUiEvent.ShowError("Deleted successfully"))
                onDeleted() // Ye callback UI ko band karne ke liye hai
            } catch (e: Exception) {
                _uiEvent.emit(CommonUiEvent.ShowError("Delete failed"))
            }
        }
    }
    @OptIn(ExperimentalTime::class)
    fun redoGeneration(entity: RecentGeneratedEntity, onResult: () -> Unit) {
        viewModelScope.launch {
            val imageBytes = if (entity.localPath != null) {
                try {
                    readLocalFile(entity.localPath)
                } catch (e: Exception) {
                    null
                }
            } else null

            if (imageBytes == null || imageBytes.isEmpty()) {
                println("❌ Redo: No image bytes available")
                return@launch
            }

            // ✅ Purani delete karo
            recentGeneratedRepository.deleteGeneratedById(entity.id)

            // ✅ isGenerating true karo — LoadingScreen dikhega
            _state.update {
                it.copy(
                    selectedImageBytes = imageBytes,
                    selectedFileName = "redo_${entity.id}.jpg",
                    isGenerating = true,
                    isFetchingImages = false,
                    generatedImages = emptyList(),
                    generatedImagesEntity = emptyList(),
                    errorMessage = null
                )
            }

            // ✅ Pehle Result screen pe jao
            onResult()

            // ✅ Phir generate karo — yeh isGenerating ko handle karega
            val email = authViewModel.state.value.email ?: ""
            val deviceId = getDeviceId()
            val creditResult = if (email.isBlank()) {
                spendCreditsUseCaseGuest(deviceId = deviceId, amount = 1)
            } else {
                spendCreditsUseCase(userEmail = email, deviceId = deviceId, amount = 1)
            }
            if (creditResult.isFailure) {
                _state.update { it.copy(isGenerating = false, errorMessage = "Not enough credits") }
                return@launch
            }
            if (!email.isNullOrBlank()) {
                authViewModel.fetchUserDetails()
            }

            val base64Image = "data:image/jpeg;base64,${imageBytes.toBase64()}"
            val prompt = buildPromptFromState(_state.value)

            val request = GenerateRoomRequest(
                initImage = base64Image,
                prompt = prompt
            )
            val result = generateRoomUseCase(request)

            when (result) {
                is ResultState.Success -> {
                    val response = result.data
                    val taskId = response.id?.toString() ?: "task_${kotlin.time.Clock.System.now().toEpochMilliseconds()}";                    val delay = response.eta?.toLong() ?: 30L
                    startImageTrackingUseCase(taskId, delay)
                    if (response.isProcessing && response.fetchUrl != null) {
                        _state.update {
                            it.copy(
                                isGenerating = false,
                                isFetchingImages = true,
                                etaSeconds = response.eta ?: 30,
                                generatedImages = emptyList(),
                                generatedImagesEntity = emptyList()
                            )
                        }

                        // ✅ Polling
                        kotlinx.coroutines.delay((response.eta ?: 30) * 1000L)
                        var fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                        var retries = 0

                        while (retries < 20) {
                            if (fetchResult is ResultState.Success) {
                                val data = fetchResult.data
                                if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                                    val images = data.availableImages
                                    val localPaths = images.map { url ->
                                        downloadAndCacheImage(
                                            url = url,
                                            fileName = "interior_${kotlin.time.Clock.System.now().toEpochMilliseconds()}.jpg"
                                        )
                                    }
                                    images.forEachIndexed { index, url ->
                                        recentGeneratedRepository.saveGenerated(
                                            RecentGeneratedEntity(
                                                imageUrl = url,
                                                localPath = localPaths.getOrNull(index)
                                            )
                                        )
                                    }
                                    _state.update {
                                        it.copy(
                                            isFetchingImages = false,
                                            generatedImages = images,
                                            generatedImagesEntity = images.mapIndexed { index, url ->
                                                RecentGeneratedEntity(
                                                    imageUrl = url,
                                                    localPath = localPaths.getOrNull(index)
                                                )
                                            }
                                        )
                                    }
                                    break
                                }
                            }
                            retries++
                            kotlinx.coroutines.delay(10_000L)
                            fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                        }
                    }
                }
                else -> {
                    _state.update { it.copy(isGenerating = false) }
                }
            }
        }
    }

}