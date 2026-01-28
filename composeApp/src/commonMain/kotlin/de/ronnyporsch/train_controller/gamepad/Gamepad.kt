package de.ronnyporsch.train_controller.gamepad

import kotlinx.coroutines.flow.Flow

expect class Gamepad(index: Int) {
    val index: Int
    suspend fun shortVibration()
    val eventFlow: Flow<GamepadEvent>
    companion object {
        val MAX_GAMEPADS : Int
        val MAX_VALUE_TRIGGER : Int
        fun getAllGamepads() : List<Gamepad>
    }
}
