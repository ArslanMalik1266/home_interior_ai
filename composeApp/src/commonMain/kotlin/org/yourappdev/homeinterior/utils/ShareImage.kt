package org.yourappdev.homeinterior.utils

expect suspend fun shareImage(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String = "interior_design.jpg"
)