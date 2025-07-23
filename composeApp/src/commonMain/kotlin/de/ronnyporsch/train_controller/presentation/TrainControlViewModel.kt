package de.ronnyporsch.train_controller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ronnyporsch.train_controller.bluetooth.Hub
import de.ronnyporsch.train_controller.domain.Player
import de.ronnyporsch.train_controller.domain.Train
import de.ronnyporsch.train_controller.domain.Train.Companion.trains
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrainControlViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrainControlUiState())
    val uiState = _uiState.asStateFlow()

    init {
        handleGamepadEvents()
        Hub.scanForHubsContinuously(viewModelScope)
    }

    fun hoverNextTrain(player: Player) {
        val index = trains.value.indexOf(player.hoveredTrain)
        val newTrain = if (index == -1 || index == trains.value.size - 1) trains.value.toList()[0] else trains.value.toList()[index + 1]
        player.hoveredTrain = newTrain
    }

    fun hoverPreviousTrain(player: Player) {
        val index = trains.value.indexOf(player.hoveredTrain)
        val newTrain = if (index == -1 || index == 0) trains.value.toList()[trains.value.size - 1] else trains.value.toList()[index - 1]
        player.hoveredTrain = newTrain
    }

    fun getAllControlledTrainsOfPlayer(player: Player): List<Train> {
        return trains.value.filter { it.currentPlayer == player }
    }

    suspend fun deselectHoveredTrain(player: Player) {
        val hoveredTrain = player.hoveredTrain ?: return
        if (hoveredTrain.currentPlayer != player) return
        hoveredTrain.setCurrentPlayer(null)
    }
}

expect fun TrainControlViewModel.handleGamepadEvents()