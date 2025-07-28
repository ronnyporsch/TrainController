package de.ronnyporsch.train_controller.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ronnyporsch.train_controller.bluetooth.Hub
import de.ronnyporsch.train_controller.domain.Player
import de.ronnyporsch.train_controller.domain.Train
import de.ronnyporsch.train_controller.domain.Train.Companion.trains
import de.ronnyporsch.train_controller.gamepad.Gamepad
import de.ronnyporsch.train_controller.gamepad.GamepadEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun handleGamepadEvents() {
        val gamepads = Gamepad.getALlGamepads() //TODO use remember here?
        gamepads.forEach { gamepad ->
            CoroutineScope(Dispatchers.IO).launch {
                gamepad.eventFlow.collect { event ->
                    val player = getPlayerForGamepad(gamepad) ?: return@collect
                    when (event) {
                        is GamepadEvent.ButtonPressed -> {
                            gamepadButtonMapping[event.button]?.let { processGamepadIntent(gamepad, it) }
                        }

                        is GamepadEvent.ButtonReleased -> {}
                        is GamepadEvent.LeftStickMoved -> {}
                        is GamepadEvent.RightStickMoved -> {}
                        is GamepadEvent.LeftTriggerMoved -> {
                            getAllControlledTrainsOfPlayer(player).forEach { train -> train.changeSpeed(triggerValueToSpeed(-event.value)) }
                        }

                        is GamepadEvent.RightTriggerMoved -> {
                            getAllControlledTrainsOfPlayer(player).forEach { train -> train.changeSpeed(triggerValueToSpeed(event.value)) }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the player that has this gamepad assigned to him.
     * If there currently is no player assigned to it, the gamepad will be assigned to the first player without a gamepad
     */
    fun getPlayerForGamepad(gamepad: Gamepad): Player? {
        val players = uiState.value.players
        var player = players.find { it.gamepadId == gamepad.index }
            if (player == null) {
                player = players.find { it.gamepadId == null }
                player?.gamepadId = gamepad.index
            }
        return player

    }

    fun triggerValueToSpeed(triggerValue: Float): Int {
        return ((triggerValue / Gamepad.MAX_VALUE_TRIGGER) * 100).toInt()
    }

    fun processGamepadIntent(gamepad: Gamepad, gamepadIntent: GamepadIntent) {
        val player = getPlayerForGamepad(gamepad) ?: return
        when (gamepadIntent) {
            is GamepadIntent.SelectHoveredTrain -> {
                if (player.hoveredTrain?.currentPlayer == player) return
                CoroutineScope(Dispatchers.IO).launch {
                    player.hoveredTrain?.setCurrentPlayer(player)
                    gamepad.shortVibration()
                }
            }

            GamepadIntent.HoverNextTrain -> hoverNextTrain(player)
            GamepadIntent.HoverPreviousTrain -> hoverPreviousTrain(player)
            GamepadIntent.ToggleReverseDirection -> CoroutineScope(Dispatchers.IO).launch { player.hoveredTrain?.toggleReverseDirection() }
            GamepadIntent.DecreaseSpeed -> CoroutineScope(Dispatchers.IO).launch { getAllControlledTrainsOfPlayer(player).forEach { train -> train.decreaseSpeed() } }
            GamepadIntent.IncreaseSpeed -> CoroutineScope(Dispatchers.IO).launch { getAllControlledTrainsOfPlayer(player).forEach { train -> train.increaseSpeed() } }
            GamepadIntent.DeselectHoveredTrain -> CoroutineScope(Dispatchers.IO).launch { deselectHoveredTrain(player) }
            GamepadIntent.Stop -> CoroutineScope(Dispatchers.IO).launch {
                getAllControlledTrainsOfPlayer(player).forEach { train ->
                    train.changeSpeed(
                        0
                    )
                }
            }

            GamepadIntent.ToggleLight -> CoroutineScope(Dispatchers.IO).launch { getAllControlledTrainsOfPlayer(player).forEach { train -> train.toggleLight() } }
        }
    }

}

//expect fun TrainControlViewModel.handleGamepadEvents()