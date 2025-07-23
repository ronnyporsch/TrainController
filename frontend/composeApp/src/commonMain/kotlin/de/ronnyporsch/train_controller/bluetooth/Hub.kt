package de.ronnyporsch.train_controller.bluetooth

import com.juul.kable.DiscoveredCharacteristic
import com.juul.kable.Identifier
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import de.ronnyporsch.train_controller.util.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

const val MANUFACTURER_CODE_LEGO = 0x0397
const val LEGO_SERVICE_UUID = "00001623-1212-efde-1623-785feabcd123"
const val WRITE_CHARACTERISTIC_UUID = "00001624-1212-efde-1623-785feabcd123"

data class Hub(
    val identifier: Identifier,
    val peripheral: Peripheral,
) {

    var writeCharacteristic: DiscoveredCharacteristic? = null

    @OptIn(ExperimentalUuidApi::class)
    suspend fun connect() {
        logger.i("Connecting to hub ${peripheral.identifier} ...")
        peripheral.connect()
        logger.i("Connected to hub ${peripheral.identifier}")
        retrieveWriteCharacteristic()
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun retrieveWriteCharacteristic() {
        val services = peripheral.services.first()
        val legoService = services?.first { it.serviceUuid.toString() == LEGO_SERVICE_UUID }
        writeCharacteristic = legoService?.characteristics?.first { it.characteristicUuid.toString() == WRITE_CHARACTERISTIC_UUID }
        logger.d("writeCharacteristic retrieved: ${writeCharacteristic?.characteristicUuid}")
    }


    suspend fun setMotorSpeed(speed: Int) {
        val command = byteArrayOf(
            0x0A, 0x00, // length
            0x81.toByte(), // Port output command
            0x00, // Port A
            0x11.toByte(), // Start power command
            0x01, // Execution flags
            speed.toByte(),
            0x64, // Max power
            0x64, // Acceleration
            0x03 // Profile
        )
        writeCharacteristic?.let { peripheral.write(it, command) }?: logger.w("writeCharacteristic is null")
    }

    suspend fun setLightIntensity(intensity: Int) {
        val command = byteArrayOf(
            0x0A,         // length
            0x00,
            0x81.toByte(),// Port output command
            0x01,         // Port A (might be 0 or 1 depending on the train)
            0x11,         // Start power command
            0x01,         // Execution flags
            intensity.toByte(),
            0x64,         // Max power
            0x64,         // Acceleration
            0x03          // Profile
        )

        writeCharacteristic?.let { peripheral.write(it, command) }?: logger.w("writeCharacteristic is null")
    }

    suspend fun setLEDColor(color: Int) {
        val command = byteArrayOf(0x07, 0x00, 0x81.toByte(), 0x32, 0x11, 0x51, 0x00, color.toByte())
        writeCharacteristic?.let { peripheral.write(it, command) }?: logger.w("writeCharacteristic is null")
    }

    companion object {
        private val _hubs = MutableStateFlow(emptyList<Hub>())
        val hubs = _hubs.asStateFlow()
        fun scanForHubsContinuously(coroutineScope: CoroutineScope) = CoroutineScope(coroutineScope.coroutineContext).launch {
            logger.i("Scanning for hubs ...")
            scanner.advertisements.collect { advertisement ->
                if (_hubs.value.any { it.identifier == advertisement.identifier }) return@collect
                if (advertisement.manufacturerData?.code == MANUFACTURER_CODE_LEGO) {
                    logger.i("Found hub: ${advertisement.identifier}")
                    val peripheral = Peripheral(advertisement)
                    val hub = Hub(advertisement.identifier, peripheral)
                    _hubs.update { it + hub }
                    launch {
                        hub.connect()
                    }
                }
            }
        }

    }
}

private val scanner = Scanner {
    logging {
        engine = SystemLogEngine
        level = Logging.Level.Warnings
        format = Logging.Format.Multiline
    }
}