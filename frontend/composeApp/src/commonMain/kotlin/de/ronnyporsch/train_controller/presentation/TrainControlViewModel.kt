package de.ronnyporsch.train_controller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ronnyporsch.train_controller.core.MyIO
import de.ronnyporsch.train_controller.domain.Player
import de.ronnyporsch.train_controller.domain.Train
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainControlViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TrainControlUiState())
    val uiState = _uiState

    init {
        handleGamepadEvents()
        viewModelScope.launch {
            while (isActive) {
                withContext(Dispatchers.MyIO) {
                    try {
                        val trains = Train.retrieveAllTrains()
                        _uiState.update { it.copy(trains = (it.trains + trains) as LinkedHashSet<Train>, error = null) }
                    } catch (e: Exception) {
                        println("Could not retrieve trains: ${e.message}")
                        _uiState.value = _uiState.value.copy(trains = LinkedHashSet(), error = "Could not retrieve trains from backend: ${e.message}")
                    }
                    delay(1000)
                }
            }
        }
    }

    fun process(intent: TrainControlIntent) {
        when (intent) {
            is TrainControlIntent.ChangeSpeed -> {
                CoroutineScope(Dispatchers.MyIO).launch {
                    val trainToChange = _uiState.value.trains.find { it.bluetoothAddress == intent.trainId }
                    trainToChange?.changeSpeed(intent.speed)
                }
            }
        }
    }

    fun hoverNextTrain(player: Player) {
        val trains = _uiState.value.trains
        val index = trains.indexOf(player.hoveredTrain)
        val newTrain = if (index == -1 || index == trains.size - 1) trains.toList()[0] else trains.toList()[index + 1]
        player.hoveredTrain = newTrain
    }

    fun hoverPreviousTrain(player: Player) {
        val trains = _uiState.value.trains
        val index = trains.indexOf(player.hoveredTrain)
        val newTrain = if (index == -1 || index == 0) trains.toList()[trains.size - 1] else trains.toList()[index -1]
        player.hoveredTrain = newTrain
    }

    fun getAllControlledTrainsOfPlayer(player: Player): List<Train> {
        return _uiState.value.trains.filter { it.currentPlayer == player }
    }

    suspend fun deselectHoveredTrain(player: Player) {
        val hoveredTrain = player.hoveredTrain ?: return
        if (hoveredTrain.currentPlayer != player) return
        hoveredTrain.setCurrentPlayer(null)
    }
}

expect fun TrainControlViewModel.handleGamepadEvents()