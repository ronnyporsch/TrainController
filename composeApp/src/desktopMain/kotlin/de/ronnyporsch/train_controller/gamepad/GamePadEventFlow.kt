package de.ronnyporsch.train_controller.gamepad

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.collections.forEach
import kotlin.time.Duration.Companion.milliseconds

class GamepadEventFlow(private val controller: Gamepad) {

    private val pollInterval = 16.milliseconds // ~60 FPS

    private var previousButtons: Int = 0
    private var previousLeftTrigger = 0
    private var previousRightTrigger = 0
    private var previousLeftStick = 0 to 0
    private var previousRightStick = 0 to 0

    val eventFlow: Flow<GamepadEvent> = flow {
        while (true) {
            if (controller.update()) {
                // Button events
                val currentButtons = controller.getPressedButtons()
                val pressed = currentButtons and previousButtons.inv()
                val released = previousButtons and currentButtons.inv()

                GamepadButton.entries
                    .forEach { button ->
                        if ((pressed and button.code) != 0) emit(GamepadEvent.ButtonPressed(controller.index, button))
                        if ((released and button.code) != 0) emit(GamepadEvent.ButtonReleased(controller.index, button))
                    }

                previousButtons = currentButtons

                // Trigger events
                val leftTrigger = controller.getLeftTrigger()
                if (leftTrigger != previousLeftTrigger) {
                    emit(GamepadEvent.LeftTriggerMoved(controller.index, leftTrigger))
                    previousLeftTrigger = leftTrigger
                }

                val rightTrigger = controller.getRightTrigger()
                if (rightTrigger != previousRightTrigger) {
                    emit(GamepadEvent.RightTriggerMoved(controller.index, rightTrigger))
                    previousRightTrigger = rightTrigger
                }

                // Stick events
                val leftStick = controller.getLeftStick()
                if (leftStick != previousLeftStick) {
                    emit(GamepadEvent.LeftStickMoved(controller.index, leftStick.first, leftStick.second))
                    previousLeftStick = leftStick
                }

                val rightStick = controller.getRightStick()
                if (rightStick != previousRightStick) {
                    emit(GamepadEvent.RightStickMoved(controller.index, rightStick.first, rightStick.second))
                    previousRightStick = rightStick
                }
            }

            delay(pollInterval)
        }
    }.flowOn(Dispatchers.Default)

    private fun Gamepad.getPressedButtons(): Int {
        var pressed = 0
        GamepadButton.entries.forEach { button ->
                if (this.isButtonPressed(button)) pressed = pressed or button.code
            }
        return pressed
    }
}
