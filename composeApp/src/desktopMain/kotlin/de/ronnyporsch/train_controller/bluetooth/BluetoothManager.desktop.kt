package de.ronnyporsch.train_controller.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class BluetoothManager actual constructor() {
    actual fun askUserToEnableBluetoothIfNotOnAlready() {
        //not possible on Desktop
    }

    private val _bluetoothStateFlow = MutableStateFlow<BluetoothState>(BluetoothState.EnabledAndPermissionGranted)
    actual val bluetoothStateFlow: StateFlow<BluetoothState> = _bluetoothStateFlow.asStateFlow()

    actual fun askUserToGrantBluetoothPermissions() {
        //not necessary on Desktop
    }

    actual fun setBluetoothError(exception: Exception) {
        _bluetoothStateFlow.value = BluetoothState.Error(exception)
    }
}