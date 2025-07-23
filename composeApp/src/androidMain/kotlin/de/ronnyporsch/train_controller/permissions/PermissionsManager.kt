package de.ronnyporsch.train_controller.permissions

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermissionManager(caller: ActivityResultCaller) {

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> get() = _hasPermission

    private val launcher = caller.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        _hasPermission.value = permissions.all { it.value }
    }

    fun requestPermissions(permissions: Array<String>) {
        launcher.launch(permissions)
    }
}
