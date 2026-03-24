package org.yourappdev.homeinterior.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpendCreditsResponse(
    val status: String? = null,
    val total_credits: Int? = null
)