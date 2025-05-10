package de.ronnyporsch.train_controller.gamepad

sealed class GamepadEvent(open val gamepadIndex: Int) {
    data class ButtonPressed(override val gamepadIndex: Int, val button: GamepadButton,) : GamepadEvent(gamepadIndex)
    data class ButtonReleased(override val gamepadIndex: Int, val button: GamepadButton) : GamepadEvent(gamepadIndex)
    data class LeftTriggerMoved(override val gamepadIndex: Int, val value: Int) : GamepadEvent(gamepadIndex)
    data class RightTriggerMoved(override val gamepadIndex: Int, val value: Int) : GamepadEvent(gamepadIndex)
    data class LeftStickMoved(override val gamepadIndex: Int, val x: Int, val y: Int) : GamepadEvent(gamepadIndex)
    data class RightStickMoved(override val gamepadIndex: Int, val x: Int, val y: Int) : GamepadEvent(gamepadIndex)
}
