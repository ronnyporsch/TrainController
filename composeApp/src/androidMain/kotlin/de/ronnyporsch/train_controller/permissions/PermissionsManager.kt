package de.ronnyporsch.train_controller.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PermissionManager<T> private constructor(private val caller: T) where T : ActivityResultCaller, T : Context {

    private val _grantedPermissions = MutableStateFlow(emptyArray<String>())
    val grantedPermissions: StateFlow<Array<String>> = _grantedPermissions
    private val launcher = caller.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { newPermissions ->
        println("granted permissions: $newPermissions")
        _grantedPermissions.update { oldPermissions -> oldPermissions + newPermissions.filter { it.value }.keys.toTypedArray() }
    }

    fun requestPermissions(permissions: Array<String>) {
        launcher.launch(permissions)
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(caller, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        lateinit var INSTANCE: PermissionManager<*>

        //has to be called during onCreate(); see https://stackoverflow.com/q/64476827
        fun <T> init(caller: T) where T : ActivityResultCaller, T : Context {
            INSTANCE = PermissionManager(caller)
        }
    }
}
