package de.ronnyporsch.train_controller

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    try {
        startBackend()
    } catch (e: Exception) {
        e.printStackTrace()
        return@application
    }
    Window(
        onCloseRequest = {
            runCatching { stopBackend() }
            exitApplication()
        },
        title = APP_NAME,
        state = WindowState(height = 700.dp, width = 800.dp),
    ) {
        App()
    }
}
