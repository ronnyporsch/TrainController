@file:OptIn(ExperimentalWasmJsInterop::class)

package de.ronnyporsch.train_controller.gamepad

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import web.gamepad.GamepadEffectParameters
import web.gamepad.GamepadHapticEffectType
import web.gamepad.dualRumble
import web.gamepad.playEffect
import web.navigator.navigator
import kotlin.time.Duration.Companion.milliseconds

@JsFun("(startDelay, duration, weakMagnitude, strongMagnitude) => ({ startDelay, duration, weakMagnitude, strongMagnitude })")
private external fun createVibrationOptions(startDelay: Int, duration: Int, weakMagnitude: Double, strongMagnitude: Double): GamepadEffectParameters

actual class Gamepad actual constructor(actual val index: Int) {

    actual val eventFlow: Flow<GamepadEvent> = flow {
        var previousButtons = emptyList<Boolean>()
        var previousButtonValues = emptyList<Double>()
        var previousAxes = emptyList<Double>()

        while (true) {
            val webGamepads = navigator.getGamepads()
            val webGamepad = if (index < webGamepads.length) webGamepads[index] else null

            if (webGamepad != null && webGamepad.connected) {
                val currentButtons = mutableListOf<Boolean>()
                val currentButtonValues = mutableListOf<Double>()
                for (i in 0 until webGamepad.buttons.length) {
                    val button = webGamepad.buttons[i]
                    currentButtons.add(button?.pressed ?: false)
                    currentButtonValues.add(button?.value ?: 0.0)
                }

                if (previousButtons.isNotEmpty()) {
                    for (i in 0 until currentButtons.size) {
                        val pressed = currentButtons[i]
                        val prevPressed = previousButtons.getOrNull(i)
                        val value = currentButtonValues[i]
                        val prevValue = previousButtonValues.getOrNull(i) ?: 0.0

                        val button = mapIndexToButton(i)
                        if (button != null) {
                            if (pressed != prevPressed) {
                                if (pressed) {
                                    emit(GamepadEvent.ButtonPressed(index, button))
                                } else {
                                    emit(GamepadEvent.ButtonReleased(index, button))
                                }
                            }
                        } else {
                            if (value != prevValue) {
                                // 6 : left trigger, 7 : right trigger
                                if (i == 6) {
                                    println(value)
                                    emit(GamepadEvent.LeftTriggerMoved(index, value.toFloat()))
                                } else if (i == 7) {
                                    emit(GamepadEvent.RightTriggerMoved(index, value.toFloat()))
                                }
                            }
                        }
                    }
                }
                previousButtons = currentButtons
                previousButtonValues = currentButtonValues

                // Axes
                val currentAxes = mutableListOf<Double>()
                for (i in 0 until webGamepad.axes.length) {
                    currentAxes.add(webGamepad.axes[i]?.toDouble() ?: 0.0)
                }

                if (previousAxes.isNotEmpty()) {
                    // Standard mapping: 0: LX, 1: LY, 2: RX, 3: RY
                    if (currentAxes.size >= 2) {
                        if (currentAxes[0] != previousAxes.getOrNull(0) || currentAxes[1] != previousAxes.getOrNull(1)) {
                            emit(GamepadEvent.LeftStickMoved(index, (currentAxes[0] * 32767).toInt(), (currentAxes[1] * -32767).toInt()))
                        }
                    }
                    if (currentAxes.size >= 4) {
                        if (currentAxes[2] != previousAxes.getOrNull(2) || currentAxes[3] != previousAxes.getOrNull(3)) {
                            emit(GamepadEvent.RightStickMoved(index, (currentAxes[2] * 32767).toInt(), (currentAxes[3] * -32767).toInt()))
                        }
                    }
                }
                previousAxes = currentAxes
            }
            delay(16.milliseconds)
        }
    }

    actual suspend fun shortVibration() {

        val webGamepads = navigator.getGamepads()
        val webGamepad = if (index < webGamepads.length) webGamepads[index] else null

        if (webGamepad != null && webGamepad.connected) {
            val effectOptions = createVibrationOptions(0, 75, 0.7, 0.7)
            webGamepad.vibrationActuator.playEffect(GamepadHapticEffectType.dualRumble, effectOptions)
        }
    }

    private fun mapIndexToButton(i: Int): GamepadButton? {
        return when (i) {
            0 -> GamepadButton.A
            1 -> GamepadButton.B
            2 -> GamepadButton.X
            3 -> GamepadButton.Y
            4 -> GamepadButton.LeftShoulder
            5 -> GamepadButton.RightShoulder
            8 -> GamepadButton.Back
            9 -> GamepadButton.Start
            10 -> GamepadButton.LeftThumb
            11 -> GamepadButton.RightThumb
            12 -> GamepadButton.DPadUp
            13 -> GamepadButton.DPadDown
            14 -> GamepadButton.DPadLeft
            15 -> GamepadButton.DPadRight
            else -> null
        }
    }

    actual companion object {
        actual const val MAX_GAMEPADS: Int = 4
        actual const val MAX_VALUE_TRIGGER: Int = 1
        actual fun getAllGamepads(): List<Gamepad> = List(MAX_GAMEPADS) { Gamepad(it) }
    }
}

