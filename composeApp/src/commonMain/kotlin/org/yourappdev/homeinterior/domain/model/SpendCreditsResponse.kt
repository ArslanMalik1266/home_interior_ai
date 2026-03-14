package org.yourappdev.homeinterior.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpendCreditsResponse(
    val status: String,
    val total_credits: Int
)