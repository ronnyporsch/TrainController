package de.ronnyporsch.train_controller.presentation

import de.ronnyporsch.train_controller.domain.Player

data class TrainControlUiState(
    val players: Set<Player> = setOf(Player(0), Player(1)),
    val error: String? = null
)
