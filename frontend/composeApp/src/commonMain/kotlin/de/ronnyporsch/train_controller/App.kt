package de.ronnyporsch.train_controller

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.ronnyporsch.train_controller.core.presentation.theme.SingularityTheme
import de.ronnyporsch.train_controller.presentation.TrainControlScreen
import de.ronnyporsch.train_controller.presentation.TrainControlViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    SingularityTheme {
        Surface(Modifier.fillMaxSize()) {
            TrainControlScreen(TrainControlViewModel())
        }
    }
}