package org.yourappdev.homeinterior.domain.model

import homeinterior.composeapp.generated.resources.Res
import homeinterior.composeapp.generated.resources.sofa
import org.jetbrains.compose.resources.DrawableResource

data class AboutToGenerateUiState(
    val selectedType: String = "Bedroom",
    val selectedStyle: String = "Cont",
    val selectedImageRes: String? = null,
    val isLoading: Boolean = false
)