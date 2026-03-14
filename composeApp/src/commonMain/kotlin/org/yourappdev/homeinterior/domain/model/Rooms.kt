package org.yourappdev.homeinterior.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rooms(
    @SerialName("rooms")
    val rooms: List<Room> = emptyList(),

    @SerialName("success")
    val success: Boolean = false,

    val message: String? = null
)