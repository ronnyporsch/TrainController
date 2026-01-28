package de.ronnyporsch.train_controller.bluetooth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

actual fun Hub.Companion.scanForHubsOnce(
    coroutineScope: CoroutineScope,
    bluetoothManager: BluetoothManager
): Job {
    throw UnsupportedOperationException("unused on Android and Desktop")
}