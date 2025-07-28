package de.ronnyporsch.train_controller.gamepad

import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

actual data class Gamepad actual constructor(actual val index: Int) {
    actual suspend fun shortVibration() {
        //not supported on Android
    }

    private val _eventFlow = MutableSharedFlow<GamepadEvent>()
    actual val eventFlow: Flow<GamepadEvent> = _eventFlow
    private val dPadState = MutableStateFlow(DPadState.CENTER)
    private val leftTriggerState = MutableStateFlow(0f)
    private val rightTriggerState = MutableStateFlow(0f)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dPadState.collect { state ->
                val button = when (state) {
                    DPadState.UP -> GamepadButton.DPadUp
                    DPadState.DOWN -> GamepadButton.DPadDown
                    DPadState.LEFT -> GamepadButton.DPadLeft
                    DPadState.RIGHT -> GamepadButton.DPadRight
                    else -> return@collect
                }
                _eventFlow.emit(GamepadEvent.ButtonPressed(index, button))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            leftTriggerState.collect { state ->
                _eventFlow.emit(GamepadEvent.LeftTriggerMoved(index, state))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            rightTriggerState.collect { state ->
                _eventFlow.emit(GamepadEvent.RightTriggerMoved(index, state))
            }
        }


    }

    actual companion object {
        actual val MAX_GAMEPADS = 4 //TODO not needed on android?
        actual val MAX_VALUE_TRIGGER = 1
        actual fun getALlGamepads(): List<Gamepad> {
            return allG
        }

        val allG: List<Gamepad> =
            InputDevice.getDeviceIds()
                .filter { InputDevice.getDevice(it)?.isGamepad() ?: false }
                .map { Gamepad(index = it) }


        /**
         * returns true if the event has been handled, false otherwise
         */
        fun handleKeyEvent(event: KeyEvent): Boolean {
            val device = event.device
            if (device != null && device.isGamepad()) {
                val allGamepads = getALlGamepads()
                allGamepads.firstOrNull { it.index == device.id }?.let { gamepad ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val button = event.keyCode
                        gamepad._eventFlow.emit(
                            GamepadEvent.ButtonPressed(
                                gamepad.index,
//                                GamepadButton.entries.find { it.code == button } ?: return@launch))
                                GamepadButton.entries.find { it.code == button }!!
                            )
                        )
                    }
                }
                return true
            }
            return false
        }

        fun handleMotionEvent(event: MotionEvent): Boolean {
            val device = event.device
            if (device != null && device.isGamepad()) {
                val allGamepads = getALlGamepads()
                allGamepads.firstOrNull { it.index == device.id }?.let { gamepad ->
                    CoroutineScope(Dispatchers.IO).launch {
                        //left stick is currently not used
                        val leftStickX = event.getAxisValue(MotionEvent.AXIS_X)
                        val leftStickY = event.getAxisValue(MotionEvent.AXIS_Y)

                        //right stick is currently not used
                        val rightStickX = event.getAxisValue(MotionEvent.AXIS_Z)
                        val rightStickY = event.getAxisValue(MotionEvent.AXIS_RZ)

                        val dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
                        val dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)
                        gamepad.dPadState.value = when {
                            dpadX == -1.0f && dpadY == 0.0f -> DPadState.LEFT
                            dpadX == 1.0f && dpadY == 0.0f -> DPadState.RIGHT
                            dpadX == 0.0f && dpadY == -1.0f -> DPadState.UP
                            dpadX == 0.0f && dpadY == 1.0f -> DPadState.DOWN
                            dpadX == -1.0f && dpadY == -1.0f -> DPadState.UP_LEFT
                            dpadX == 1.0f && dpadY == -1.0f -> DPadState.UP_RIGHT
                            dpadX == -1.0f && dpadY == 1.0f -> DPadState.DOWN_LEFT
                            dpadX == 1.0f && dpadY == 1.0f -> DPadState.DOWN_RIGHT
                            else -> DPadState.CENTER
                        }
                        gamepad.leftTriggerState.value = event.getAxisValue(MotionEvent.AXIS_LTRIGGER)
                        gamepad.rightTriggerState.value = event.getAxisValue(MotionEvent.AXIS_RTRIGGER)
                    }
                }
                return true
            }
            return false
        }

        fun InputDevice.isGamepad(): Boolean {
            return (sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
                    (sources and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
        }
    }
}

enum class DPadState {
    UP, DOWN, LEFT, RIGHT, CENTER, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT;
}