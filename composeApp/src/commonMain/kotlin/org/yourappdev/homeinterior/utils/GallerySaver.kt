package org.yourappdev.homeinterior.utils

expect suspend fun saveImageToGallery(
    imageBytes: ByteArray?,
    imageUrl: String?,
    fileName: String = "home_interior.jpg"
): Boolean