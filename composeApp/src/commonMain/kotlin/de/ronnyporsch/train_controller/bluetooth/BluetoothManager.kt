package de.ronnyporsch.train_controller.bluetooth

import kotlinx.coroutines.flow.StateFlow

expect class BluetoothManager() {
    val bluetoothStateFlow: StateFlow<BluetoothState>
    fun askUserToEnableBluetoothIfNotOnAlready()
    fun askUserToGrantBluetoothPermissions()
    fun setBluetoothError(exception: Exception)
}
