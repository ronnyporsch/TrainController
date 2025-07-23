package de.ronnyporsch.train_controller.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun askUserToEnableBluetoothIfNotOnAlready(activity: Activity) {

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    if (bluetoothAdapter == null) {
        return
    }
    if (!bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(activity, enableBtIntent, 1002, null)
    }
}

fun bluetoothEnabledFlow(context: Context): Flow<Boolean> = callbackFlow {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    // Emit the current state immediately
    trySend(bluetoothAdapter?.isEnabled == true)

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> trySend(true).isSuccess
                    BluetoothAdapter.STATE_OFF -> trySend(false).isSuccess
                }
            }
        }
    }

    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    context.registerReceiver(receiver, filter)

    awaitClose { context.unregisterReceiver(receiver) }
}

