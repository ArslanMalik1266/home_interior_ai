package com.webscare.interiorismai.data.mapper

import com.webscare.interiorismai.domain.model.Room
import com.webscare.interiorismai.domain.model.RoomUi


fun Room.toUi(): RoomUi {
    return RoomUi(
        id = id,
        imageUrl = image_url,
        roomStyle = room_style,
        roomType = room_type,
        isTrending = is_trending,
        createdAt = created_at,
        updatedAt = updated_at,
        colors = color_palette.mapNotNull { it.toColorOrNull() },
        cardHeight = listOf(150, 180, 210, 240, 280).random()
    )
}