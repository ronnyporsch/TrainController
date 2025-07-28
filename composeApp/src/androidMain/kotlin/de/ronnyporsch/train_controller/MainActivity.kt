package de.ronnyporsch.train_controller

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.ronnyporsch.train_controller.bluetooth.askUserToEnableBluetoothIfNotOnAlready
import de.ronnyporsch.train_controller.bluetooth.bluetoothEnabledFlow
import de.ronnyporsch.train_controller.gamepad.Gamepad
import de.ronnyporsch.train_controller.gamepad.Gamepad.Companion.isGamepad
import de.ronnyporsch.train_controller.permissions.PermissionManager
import de.ronnyporsch.train_controller.permissions.requiredPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionManager = PermissionManager(this)
        permissionManager.requestPermissions(requiredPermissions)
        CoroutineScope(Dispatchers.IO).launch {
            permissionManager.hasPermission.collect {
                println("PERMISSION: $it")
                if (it) askUserToEnableBluetoothIfNotOnAlready(this@MainActivity)
            }
        }
        setContent {
            val permissionsAvailable by permissionManager.hasPermission.collectAsStateWithLifecycle()
            if (!permissionsAvailable) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Button({ permissionManager.requestPermissions(requiredPermissions) }) {
                        Text("Grant Bluetooth permissions")
                    }
                }
                return@setContent
            }
            val isBluetoothEnabled by bluetoothEnabledFlow(this).collectAsStateWithLifecycle(false)
            if (!isBluetoothEnabled) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Button({ askUserToEnableBluetoothIfNotOnAlready(this@MainActivity) }) {
                        Text("Enable Bluetooth")
                    }
                }
                return@setContent
            }
            App()

        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (event.device.isGamepad()) {
            if (Gamepad.handleKeyEvent(event)) {
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.device.isGamepad()) {
            if (Gamepad.handleMotionEvent(event)) {
                return true
            }
        }
        return super.dispatchGenericMotionEvent(event)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}