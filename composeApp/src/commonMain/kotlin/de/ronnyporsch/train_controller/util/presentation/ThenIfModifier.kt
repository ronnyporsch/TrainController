package de.ronnyporsch.train_controller.util.presentation

import androidx.compose.ui.Modifier

fun Modifier.thenIf(condition: Boolean, modifier: Modifier): Modifier {
    return if (condition) then(modifier) else this
}
