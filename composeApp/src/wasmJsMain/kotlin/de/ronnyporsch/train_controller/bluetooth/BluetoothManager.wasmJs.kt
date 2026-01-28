package de.ronnyporsch.train_controller.bluetooth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class BluetoothManager actual constructor() {
    actual fun askUserToEnableBluetoothIfNotOnAlready() {
        //not necessary on wasm
    }

    actual val bluetoothStateFlow: StateFlow<BluetoothState>
        field = MutableStateFlow<BluetoothState>(
            if (isBluetoothApiSupported()) {
                BluetoothState.EnabledAndPermissionGranted
            } else BluetoothState.NotSupported
        )

    actual fun askUserToGrantBluetoothPermissions() {
        //not necessary on wasm
    }

    actual fun setBluetoothError(exception: Exception) {
        bluetoothStateFlow.value = BluetoothState.Error(exception)
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => typeof navigator !== 'undefined' && 'bluetooth' in navigator")
private external fun isBluetoothApiSupported(): Boolean

