package de.ronnyporsch.train_controller.bluetooth

import com.juul.kable.Options
import com.juul.kable.requestPeripheral
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
actual fun Hub.Companion.scanForHubsOnce(
    coroutineScope: CoroutineScope,
    bluetoothManager: BluetoothManager
) = CoroutineScope(coroutineScope.coroutineContext).launch {
    val options = Options {
        filters {
            match {
                services = listOf(Uuid.parse(LEGO_SERVICE_UUID))
            }
        }
    }

    try {
        val peripheral = requestPeripheral(options)
        Hub.addAndConnectHub(peripheral, coroutineScope)
    } catch (e: Exception) {
        bluetoothManager.setBluetoothError(e)
    }
}
