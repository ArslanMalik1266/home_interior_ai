package org.yourappdev.homeinterior.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class VerifyResponse(
    val status: String,
    val message: String? = null,
    val auth_provider: String? = null,

)

