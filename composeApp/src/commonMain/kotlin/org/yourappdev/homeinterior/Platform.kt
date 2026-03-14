package org.yourappdev.homeinterior

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform