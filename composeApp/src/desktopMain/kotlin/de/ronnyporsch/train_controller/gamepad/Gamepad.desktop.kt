package de.ronnyporsch.train_controller.gamepad

import kotlinx.coroutines.delay

actual class Gamepad actual constructor(actual val index: Int) {
    private val state = XInput.XInputState()
    actual val eventFlow = GamepadEventFlow(this).eventFlow

    val isConnected: Boolean
        get() = XInput.INSTANCE.XInputGetState(index, state) == XInput.ERROR_SUCCESS

    fun update(): Boolean {
        return isConnected
    }

    fun isButtonPressed(button: GamepadButton): Boolean {
        return (state.Gamepad.wButtons.toInt() and button.code) != 0
    }

    fun getLeftTrigger(): Int = state.Gamepad.bLeftTrigger.toInt() and 0xFF
    fun getRightTrigger(): Int = state.Gamepad.bRightTrigger.toInt() and 0xFF

    fun getLeftStick(): Pair<Int, Int> =
        Pair(state.Gamepad.sThumbLX.toInt(), state.Gamepad.sThumbLY.toInt())

    fun getRightStick(): Pair<Int, Int> =
        Pair(state.Gamepad.sThumbRX.toInt(), state.Gamepad.sThumbRY.toInt())

    fun getPressedButtons(): Int {
        var pressed = 0
        GamepadButton.entries.forEach { button ->
            if (this.isButtonPressed(button)) pressed = pressed or button.code
        }
        return pressed
    }

    private fun setVibration(leftMotor: Int, rightMotor: Int) {
        val vib = XInput.XInputVibration().apply {
            wLeftMotorSpeed = leftMotor.coerceIn(0..65535).toShort()
            wRightMotorSpeed = rightMotor.coerceIn(0..65535).toShort()
        }
        XInput.INSTANCE.XInputSetState(index, vib)
    }

    actual suspend fun shortVibration() {
        setVibration(45000, 45000)
        delay(75)
        stopVibration()
    }

    private fun stopVibration() {
        setVibration(0, 0)
    }
    actual companion object {
        actual const val MAX_GAMEPADS = 4
        actual const val MAX_VALUE_TRIGGER = 255
        actual fun getALlGamepads() = List(MAX_GAMEPADS) { index -> Gamepad(index) }
    }
}