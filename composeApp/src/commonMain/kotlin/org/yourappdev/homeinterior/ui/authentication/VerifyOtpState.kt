package org.yourappdev.homeinterior.ui.authentication

import org.yourappdev.homeinterior.domain.model.DeviceLinkResult

data class VerifyOtpState(
    val isLoading: Boolean = false,
    val result: DeviceLinkResult? = null,
    val error: String? = null
)