package org.yourappdev.homeinterior

class JVMPlatform : Platform {
    override val name: String = "HELLO I AM DESKTOP"
}

actual fun getPlatform(): Platform = JVMPlatform()