package de.ronnyporsch.train_controller.presentation

import de.ronnyporsch.train_controller.domain.Player
import de.ronnyporsch.train_controller.domain.Train

data class TrainControlUiState(
    val trains: LinkedHashSet<Train> = LinkedHashSet(),
    val players: Set<Player> = setOf(Player(0), Player(1)),
    val error: String? = null
)
