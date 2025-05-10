package de.ronnyporsch.train_controller.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.ronnyporsch.train_controller.data.restClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
data class Train(
    val bluetoothAddress: Long,
    val name: String,
) {
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
        restClient.post("/api/Train/$bluetoothAddress/setSpeed?speed=$speedToSend")
    }

    suspend fun changeLightIntensity(intensity: Int) {
        this.lightIntensity = intensity
        restClient.post("/api/Train/$bluetoothAddress/setLightIntensity?lightIntensity=$intensity")
    }

    suspend fun toggleLight() {
        if (lightIntensity == 0) {
            changeLightIntensity(100)
        } else
            changeLightIntensity(0)
    }

    suspend fun changeStatusLEDColor(color: PlayerColor) {
        this.statusLEDColor = color
        restClient.post("/api/Train/$bluetoothAddress/setLEDColor?colorCode=${color.trainColorCode}")
    }

    companion object {
        const val MAXIMUM_SPEED = 100
        suspend fun retrieveAllTrains(): LinkedHashSet<Train> {
            return restClient.get("/api/Train/").body<LinkedHashSet<Train>>()
        }
    }
}