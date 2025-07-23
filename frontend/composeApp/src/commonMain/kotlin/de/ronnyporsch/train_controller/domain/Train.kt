package de.ronnyporsch.train_controller.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.juul.kable.ExperimentalApi
import de.ronnyporsch.train_controller.bluetooth.Hub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class Train(
    val hub: Hub
) {
    @OptIn(ExperimentalApi::class)
    val name = hub.peripheral.name ?: "Unknown name"
    var speed by mutableStateOf(0)
        private set
    var lightIntensity by mutableStateOf(0)
        private set
    var statusLEDColor by mutableStateOf(PlayerColor.WHITE)
        private set
    var reverseDirection by mutableStateOf(false)
        private set
    var currentPlayer: Player? by mutableStateOf(null)
        private set

    suspend fun toggleReverseDirection() {
        reverseDirection = !reverseDirection
        changeSpeed(0)
    }

    suspend fun setCurrentPlayer(player: Player?) {
        currentPlayer = player
        changeStatusLEDColor(player?.color ?: PlayerColor.WHITE)
    }

    suspend fun increaseSpeed() {
        if (speed > (MAXIMUM_SPEED - 10)) {
            changeSpeed(100)
        } else {
            changeSpeed(speed + 10)
        }
    }

    suspend fun decreaseSpeed() {
        if (speed < (-MAXIMUM_SPEED + 10)) {
            changeSpeed(-100)
        } else {
            changeSpeed(speed - 10)
        }
    }

    suspend fun changeSpeed(speed: Int) {
        this.speed = speed
        val speedToSend = if (reverseDirection) -speed else speed
        hub.setMotorSpeed(speedToSend)
    }

    suspend fun changeLightIntensity(intensity: Int) {
        this.lightIntensity = intensity
        hub.setLightIntensity(intensity)
    }

    suspend fun toggleLight() {
        if (lightIntensity == 0) {
            changeLightIntensity(100)
        } else
            changeLightIntensity(0)
    }

    suspend fun changeStatusLEDColor(color: PlayerColor) {
        this.statusLEDColor = color
        hub.setLEDColor(color.trainColorCode)
    }

    companion object {
        const val MAXIMUM_SPEED = 100

        val trains = Hub.hubs
            .map { hubsList: List<Hub> -> hubsList.map { hub: Hub -> Train(hub) } }
            .stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, emptyList())
    }
}