package de.ronnyporsch.train_controller

import de.ronnyporsch.train_controller.permissions.PermissionManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.ronnyporsch.train_controller.gamepad.Gamepad
import de.ronnyporsch.train_controller.gamepad.Gamepad.Companion.isGamepad

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        INSTANCE = this
        PermissionManager.init(this)
        setContent {
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

    companion object {
        var INSTANCE: MainActivity? = null
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}