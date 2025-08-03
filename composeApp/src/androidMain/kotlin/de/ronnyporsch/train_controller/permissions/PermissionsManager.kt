package de.ronnyporsch.train_controller.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.lang.ref.WeakReference

class PermissionManager private constructor(caller: ActivityResultCaller) {

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
            context.get()?.let { ContextCompat.checkSelfPermission(it, permission) == PackageManager.PERMISSION_GRANTED } == true
        }
    }

    companion object {
        lateinit var INSTANCE: PermissionManager
        private lateinit var context: WeakReference<Context>

        //has to be called during onCreate(); see https://stackoverflow.com/q/64476827
        fun <T> init(caller: T) where T : ActivityResultCaller, T : Context {
            INSTANCE = PermissionManager(caller)
            context = WeakReference(caller as Context)
        }
    }
}
