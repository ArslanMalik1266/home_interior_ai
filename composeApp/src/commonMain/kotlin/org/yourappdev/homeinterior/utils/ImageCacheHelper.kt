package org.yourappdev.homeinterior.utils

expect suspend fun downloadAndCacheImage(
    url: String,
    fileName: String
): String?

expect fun getImageModel(path: String?): Any?
