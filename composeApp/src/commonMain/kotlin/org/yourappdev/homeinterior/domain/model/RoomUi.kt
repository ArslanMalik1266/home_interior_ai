package org.yourappdev.homeinterior.domain.model

import androidx.compose.ui.graphics.Color

data class RoomUi(
    val id: Int,
    val imageUrl: String,
    val roomStyle: String,
    val roomType: String,
    val isTrending: Int,
    val createdAt: String,
    val updatedAt: String,
    val colors: List<Color>,
    val cardHeight: Int
)