package org.yourappdev.homeinterior

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Whats your version or platform, ${platform.name}!"
    }
}