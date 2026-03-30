package org.yourappdev.homeinterior.ui.CreateAndExplore

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity

sealed class RoomEvent {
    // Filter & Search Events
    data class OnSearchQueryChange(val query: String) : RoomEvent()
    data object OnApplyFilters : RoomEvent()
    object OnFilterClick : RoomEvent()
    object OnDismissFilterSheet : RoomEvent()
    object OnResetLoading : RoomEvent()
    object OnClearFilters : RoomEvent()

    data class SetImageBytes(val bytes: ByteArray, val fileName: String) : RoomEvent()

    data class ShowSelectedBundle(val bundle: List<RecentGeneratedEntity>) : RoomEvent()

    data class OnGenerateClick(
        val imageBytes: ByteArray,
        val fileName: String
    ) : RoomEvent()

    data class OnCancelGeneration(val taskId: String) : RoomEvent()


    data class OnPurchasePlan(val price: String) : RoomEvent()
    data object ClearPurchaseState : RoomEvent()
    data class OnTempFilterChange(val filterState: FilterState) : RoomEvent()
    data class OnToggleFilterSection(val section: FilterSection) : RoomEvent()

    // Generate Screen Events
    data class SetImage(val imageDetails: GalleryPhotoResult) : RoomEvent()
    data class OnPageChange(val page: Int) : RoomEvent()
    object OnNextPage : RoomEvent()
    object OnPreviousPage : RoomEvent()
    data class OnRoomTypeSelected(val roomType: String) : RoomEvent()
    data class OnRoomSearchQueryChange(val query: String) : RoomEvent()
    data class OnRoomSearchExpandedChange(val isExpanded: Boolean) : RoomEvent()
    data class OnStyleSelected(val styleId: Int) : RoomEvent()
    data class OnStyleSearchQueryChange(val query: String) : RoomEvent()
    data class OnStyleSearchExpandedChange(val isExpanded: Boolean) : RoomEvent()
    data class OnPaletteSelected(val paletteId: Int) : RoomEvent()
    object OnGenerationComplete : RoomEvent()
}