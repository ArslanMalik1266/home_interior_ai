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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val listLock = Mutex()
    private val _state = MutableStateFlow(RoomUiState())
    val state: StateFlow<RoomUiState> = _state.asStateFlow()
    private val _tasksProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val tasksProgress = _tasksProgress.asStateFlow()
    @OptIn(ExperimentalTime::class)
    val newTaskId = Clock.System.now().toEpochMilliseconds().toString()

    private val _generationProgress = MutableStateFlow(0f)
    val generationProgress = _generationProgress.asStateFlow()
    private var timerJob: kotlinx.coroutines.Job? = null
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
                        selectedImage = "image_picked",
                        currentTaskId = null,
                        isFetchingImages = false
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
                // 1. Har generation ka apna unique ID aur settings freeze karein
                val newTaskId = Clock.System.now().toEpochMilliseconds().toString()
                val capturedPrompt = buildPromptFromState(_state.value)
                val capturedImageBytes = event.imageBytes

                // Credits check (Aapka existing logic)
                val currentCredits = if (authViewModel.state.value.email.isNullOrBlank()) {
                    authViewModel.guestSession.value?.totalCredits ?: 0
                } else {
                    authViewModel.state.value.totalCredits
                }

                if (currentCredits <= 0) {
                    viewModelScope.launch { _uiEvent.emit(ShowError("Not enough credits")) }
                    return
                }

                // Task count barhayein taake UI mein loader dikhayi de
                _state.update { it.copy(
                    activeTasksCount = it.activeTasksCount + 1,
                    isFetchingImages = true,
                    isGenerating = true,
                    errorMessage = null,
                    generatedCount = 3,

                    // ✅ Sabse Zaroori Line:
                    // Naya task shuru hote hi purana data saaf kar do takke
                    // ResultScreen ko 0 images milen aur wo 3 loading boxes dikhaye
                    generatedImagesEntity = listOf(
                        RecentGeneratedEntity(
                            imageUrls = emptyList(),
                            localPaths = emptyList(),
                            bundleId = newTaskId
                        )
                    )
                )}


                viewModelScope.launch {
                    try {
                        // Credits spend logic... (Existing code)
                        val email = authViewModel.state.value.email ?: ""
                        val deviceId = getDeviceId()
                        val creditResult = if (email.isBlank()) spendCreditsUseCaseGuest(deviceId, 1) else spendCreditsUseCase(email, deviceId, 1)

                        if (creditResult.isFailure) {
                            _state.update { it.copy(activeTasksCount = (it.activeTasksCount - 1).coerceAtLeast(0)) }
                            return@launch
                        }

                        // Generation request (captured variables ke saath)
                        val base64Image = "data:image/jpeg;base64,${capturedImageBytes.toBase64()}"
                        val request = GenerateRoomRequest(initImage = base64Image, prompt = capturedPrompt)

                        // 3 Parallel Calls
                        val results = awaitAll(
                            async { generateRoomUseCase(request) },
                            async { generateRoomUseCase(request) },
                            async { generateRoomUseCase(request) }
                        )
                        results.forEachIndexed { index, result ->
                            if (result is ResultState.Success) {
                                println("DEBUG_ETA: Task $index eta = ${result.data.eta}")
                                println("DEBUG_ETA: Task $index fetchUrl = ${result.data.fetchUrl}")
                                println("DEBUG_ETA: Task $index isProcessing = ${result.data.isProcessing}")
                            }
                        }
                        _state.update { it.copy(
                            isGenerating = false,
                            isFetchingImages = true,
                            generatedImagesEntity = listOf(
                                RecentGeneratedEntity(
                                    imageUrls = emptyList(),
                                    localPaths = emptyList(),
                                    bundleId = newTaskId
                                )
                            )
                        )}

                        val maxEta = results.filter { it is ResultState.Success }
                            .map { (it as ResultState.Success).data.eta ?: 30 }
                            .maxOrNull() ?: 30
                        // ✅ Har task ka apna independent timer launch karein
                        launch {
                            for (seconds in 1..200) {
                                kotlinx.coroutines.delay(1000L)
                                val progress = (seconds.toFloat() / maxEta.toFloat()).coerceAtMost(0.99f)
                                _tasksProgress.update { it + (newTaskId to progress) }

                                // Agar task khatam ho jaye to timer rok dein
                                val isStillActive = _tasksProgress.value.containsKey(newTaskId)
                                if (!isStillActive) break                            }
                        }

                        val allGeneratedUrls = mutableListOf<String>()
                        val allLocalPaths = mutableListOf<String>()
                        var successCount = 0

                        // Fetching Logic
                        results.forEachIndexed { index, result ->
                            if (result is ResultState.Success) {
                                val response = result.data
                                if (response.isProcessing && response.fetchUrl != null) {
                                    launch {
                                        var retries = 0
                                        while (retries < 30) {
                                            val fetchResult = fetchGeneratedRoomUseCase(response.fetchUrl)
                                            if (fetchResult is ResultState.Success) {
                                                val data = fetchResult.data
                                                if (!data.isProcessing && data.availableImages.isNotEmpty()) {
                                                    val imageUrl = data.availableImages.first()
                                                    val localPath = downloadAndCacheImage(imageUrl, "room_${newTaskId}_$index.jpg")

                                                    listLock.withLock {
                                                        allGeneratedUrls.add(imageUrl)
                                                        allLocalPaths.add(localPath ?: "")
                                                        successCount++
                                                    }

                                                    _state.update { s ->
                                                        s.copy(
                                                            generatedImagesEntity = listOf(
                                                                RecentGeneratedEntity(
                                                                    imageUrls = allGeneratedUrls.toList(),
                                                                    localPaths = allLocalPaths.toList(),
                                                                    bundleId = newTaskId
                                                                )
                                                            )
                                                        )
                                                    }
                                                    if (successCount >= 3) {
                                                        // Bundle Complete! Save to DB
                                                        val bundleToSave = RecentGeneratedEntity(
                                                            imageUrls = allGeneratedUrls.toList(),
                                                            localPaths = allLocalPaths.toList(),
                                                            bundleId = newTaskId
                                                        )
                                                        recentGeneratedRepository.saveGenerated(bundleToSave)

                                                        // Clean up this task
                                                        _tasksProgress.update { it - newTaskId }
                                                        _state.update { s -> s.copy(
                                                            activeTasksCount = (s.activeTasksCount - 1).coerceAtLeast(0),
                                                            isFetchingImages = s.activeTasksCount > 1
                                                        )}
                                                    }
                                                    break
                                                }
                                            }
                                            retries++
                                            kotlinx.coroutines.delay(5000L)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(activeTasksCount = (it.activeTasksCount - 1).coerceAtLeast(0)) }
                    }
                }
            }
            is RoomEvent.OnGenerationComplete -> {
                _tasksProgress.update { emptyMap() }
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
    fun redoGeneration(
        entity: RecentGeneratedEntity,
        indexToReplace: Int, // Naya parameter takay pata ho konsi image badalni hai
        onResult: () -> Unit
    ) {
        viewModelScope.launch {
            // 1. Sirf wo image bytes uthao jise redo karna hai
            val targetPath = entity.localPaths.getOrNull(indexToReplace)
            val imageBytes = if (targetPath != null) {
                try { readLocalFile(targetPath) } catch (e: Exception) { null }
            } else null

            if (imageBytes == null || imageBytes.isEmpty()) {
                println("❌ Redo: No image bytes found for index $indexToReplace")
                return@launch
            }

            // 2. UI State update (Loading dikhane ke liye)
            _state.update {
                it.copy(
                    isGenerating = true,
                    isFetchingImages = false,
                    errorMessage = null
                )
            }
            onResult()

            // 3. Credits check aur Generation (Same purana process)
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

            val base64Image = "data:image/jpeg;base64,${imageBytes.toBase64()}"
            val prompt = buildPromptFromState(_state.value)
            val request = GenerateRoomRequest(initImage = base64Image, prompt = prompt)

            val result = generateRoomUseCase(request)

            if (result is ResultState.Success && result.data.fetchUrl != null) {
                _state.update { it.copy(isGenerating = false, isFetchingImages = true) }

                var retries = 0
                while (retries < 20) {
                    val fetchResult = fetchGeneratedRoomUseCase(result.data.fetchUrl!!)
                    if (fetchResult is ResultState.Success) {
                        val data = fetchResult.data
                        if (!data.isProcessing && data.availableImages.isNotEmpty()) {

                            val newUrl = data.availableImages.first()
                            val newPath = downloadAndCacheImage(newUrl, "redo_idx${indexToReplace}_${Clock.System.now().toEpochMilliseconds()}.jpg")

                            // ✅ MAIN LOGIC: Purani list lo aur sirf target index update karo
                            val updatedUrls = entity.imageUrls.toMutableList().apply {
                                if (size > indexToReplace) set(indexToReplace, newUrl)
                            }
                            val updatedPaths = entity.localPaths.toMutableList().apply {
                                if (size > indexToReplace) set(indexToReplace, newPath ?: "")
                            }

                            // ✅ DB UPDATE: Purana record delete nahi hoga, sirf data replace hoga
                            val updatedBundle = entity.copy(
                                imageUrls = updatedUrls,
                                localPaths = updatedPaths
                            )
                            recentGeneratedRepository.saveGenerated(updatedBundle)

                            // 4. Result screen ko updated bundle dikhao
                            _state.update { it.copy(
                                isFetchingImages = false,
                                generatedImagesEntity = listOf(updatedBundle)
                            )}
                            break
                        }
                    }
                    retries++
                    kotlinx.coroutines.delay(5000L)
                }
            } else {
                _state.update { it.copy(isGenerating = false) }
            }
        }
    }
    fun startGlobalTimer() {
        timerJob?.cancel()
        _generationProgress.value = 0f
        val maxEtaFromApi = _state.value.imageEtaSeconds.maxOrNull() ?: 30
        timerJob = viewModelScope.launch {
            for (seconds in 1..200) {
                kotlinx.coroutines.delay(1000L)
                val currentProgress = seconds.toFloat() / maxEtaFromApi.toFloat()
                if (currentProgress >= 0.99f) {
                    _generationProgress.value = 0.99f
                } else {
                    _generationProgress.value = currentProgress
                }

                if (!_state.value.isFetchingImages) {
                    _generationProgress.value = 1f
                    break
                }
            }
        }
    }
    fun prepareForNewGeneration() {
        _state.update { it.copy(isFetchingImages = false) }
    }


}