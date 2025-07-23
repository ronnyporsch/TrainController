package de.ronnyporsch.train_controller

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application


fun main() = application {
    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = APP_NAME,
        state = WindowState(height = 800.dp, width = 1000.dp),
    ) {
        App()
    }
}
