package org.yourappdev.homeinterior

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.yourappdev.homeinterior.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "homeinterior",
    ) {
        App()
    }
}