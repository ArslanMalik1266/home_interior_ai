package org.yourappdev.homeinterior.ui.CreateAndExplore

import androidx.compose.ui.graphics.Color
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.serialization.Serializable
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomResponse
import org.yourappdev.homeinterior.domain.model.Room
import org.yourappdev.homeinterior.domain.model.RoomUi
import org.yourappdev.homeinterior.domain.model.Rooms
import org.yourappdev.homeinterior.ui.Generate.UiScreens.ColorPalette
import org.yourappdev.homeinterior.ui.Generate.UiScreens.InteriorStyle

data class RoomUiState(
    val getRoomsResponse: ResultState<Rooms> = ResultState.Stable,
    val trendingRooms: List<RoomUi> = emptyList(),
    val allRooms: List<RoomUi> = emptyList(),
    val filteredRooms: List<RoomUi> = emptyList(),
    val decodedImageBytes: List<ByteArray> = emptyList(),
    val searchQuery: String = "",
    val filterState: FilterState = FilterState(),
    val filterCount: Int = 0,
    val errorMessage: String? = null,
    val tempFilterCount: Int = 0,
    val isLoading: Boolean = true,
    val showFilterSheet: Boolean = false,
    val tempFilterState: FilterState = FilterState(),
    val expandedRoomType: Boolean = false,
    val expandedStyle: Boolean = false,
    val expandedColor: Boolean = false,
    val expandedFormat: Boolean = false,
    val expandedPrice: Boolean = false,
    val availableRoomTypes: List<String> = emptyList(),
    val availableStyles: List<InteriorStyle> = emptyList(),
    val availableStylesString: List<String> = emptyList(),
    val availableColors: List<ColorPalette> = emptyList(),
    val recentGeneratedImages: List<List<String>> = emptyList(),
    val selectedImage: String? = null,
    val currentPage: Int = 0,
    val pageCount: Int = 4,
    val selectedRoomType: String? = availableRoomTypes.firstOrNull(),
    val roomSearchQuery: String = "",
    val isRoomSearchExpanded: Boolean = false,
    val selectedStyleName: String? = availableStyles.firstOrNull()?.name,
    val styleSearchQuery: String = "",
    val isStyleSearchExpanded: Boolean = false,
    val selectedPaletteId: Int? = availableColors.firstOrNull()?.id,
    val isGenerating: Boolean = false,
    val generatedImages: List<String> = emptyList(),
    val jobId: String? = null,
    val generatedRoom: GenerateRoomResponse? = null,
    val selectedImageBytes: ByteArray? = null,
    val selectedFileName: String? = null,
    val isPurchasing: Boolean = false,
    val purchaseSuccess: String? = null,
    val purchaseError: String? = null,
    val generatedCount: Int = 0,
    val generatedImagesEntity: List<RecentGeneratedEntity> = emptyList(),
    val isFetchingImages: Boolean = false,
    val etaSeconds: Int = 0,


    )

data class FilterState(
    val selectedRoomTypes: Set<String> = emptySet(),
    val selectedStyles: Set<String> = emptySet(),
    val selectedColors: Set<Int> = emptySet(),
    val selectedFormats: Set<String> = emptySet(),
    val selectedPrices: Set<String> = emptySet()
)
enum class FilterSection {
    ROOM_TYPE, STYLE, COLOR, FORMAT, PRICE
}

data class RoomDraft(
    val imageBytes: ByteArray,
    val roomType: String? = null,
    val styleName: String? = null,
    val paletteId: Int? = null,
    val currentPage: Int = 0
)
@Serializable
data class User(
    val id: Int,
    val fullname: String,
    val email: String,
    val image: String? = null
)
