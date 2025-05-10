package de.ronnyporsch.train_controller.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Player(
    val index : Int,
) {
    var hoveredTrain by mutableStateOf<Train?>(null)

    val color: PlayerColor
        get() = when (index) {
            0 -> PlayerColor.GREEN
            1 -> PlayerColor.BLUE
            2 -> PlayerColor.PURPLE
            3 -> PlayerColor.ORANGE
            else -> throw IllegalStateException("Invalid player index: $index")
        }
}
