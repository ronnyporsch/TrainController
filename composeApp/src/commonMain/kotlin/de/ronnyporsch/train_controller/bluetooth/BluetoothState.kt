package de.ronnyporsch.train_controller.bluetooth

sealed interface BluetoothState {
    data object NotSupported : BluetoothState
    data object DisabledAndPermissionDenied : BluetoothState
    data object DisabledAndPermissionGranted : BluetoothState
    data object EnabledAndPermissionGranted : BluetoothState
    data class Error(val exception: Exception) : BluetoothState

}