package de.ronnyporsch.train_controller.presentation

import de.ronnyporsch.train_controller.gamepad.Gamepad
import de.ronnyporsch.train_controller.gamepad.GamepadEvent
import de.ronnyporsch.train_controller.gamepad.GamepadEventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual fun TrainControlViewModel.handleGamepadEvents() {
    val gamepads = List(Gamepad.MAX_GAMEPADS) { index -> Gamepad(index) }
    gamepads.forEach { gamepad ->
        CoroutineScope(Dispatchers.IO).launch {
            GamepadEventFlow(gamepad).eventFlow.collect { event ->
                val player = uiState.value.players.find { it.index == event.gamepadIndex } ?: return@collect
                when (event) {
                    is GamepadEvent.ButtonPressed -> gamepadButtonMapping[event.button]?.let { processGamepadIntent(gamepad, it) }
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

fun triggerValueToSpeed(triggerValue: Int): Int {
    return ((triggerValue.toDouble() / Gamepad.MAX_VALUE_TRIGGER) * 100).toInt()
}

fun TrainControlViewModel.processGamepadIntent(gamepad: Gamepad, gamepadIntent: GamepadIntent) {
    val player = uiState.value.players.find { it.index == gamepad.index } ?: return
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
        GamepadIntent.Stop -> CoroutineScope(Dispatchers.IO).launch { getAllControlledTrainsOfPlayer(player).forEach { train -> train.changeSpeed(0) } }
        GamepadIntent.ToggleLight -> CoroutineScope(Dispatchers.IO).launch { getAllControlledTrainsOfPlayer(player).forEach { train -> train.toggleLight() } }
    }
}