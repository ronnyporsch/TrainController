package de.ronnyporsch.train_controller.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import de.ronnyporsch.train_controller.MainActivity
import de.ronnyporsch.train_controller.permissions.PermissionManager
import de.ronnyporsch.train_controller.permissions.bluetoothPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val activity = MainActivity.INSTANCE

actual class BluetoothManager : DefaultLifecycleObserver {

    private val _bluetoothStateFlow = MutableStateFlow<BluetoothState>(BluetoothState.DisabledAndPermissionDenied)
    actual val bluetoothStateFlow: StateFlow<BluetoothState> = _bluetoothStateFlow.asStateFlow()
    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> _bluetoothStateFlow.value = BluetoothState.EnabledAndPermissionGranted
                    BluetoothAdapter.STATE_OFF -> {
                        when (PermissionManager.INSTANCE.hasPermissions(bluetoothPermissions)) {
                            true -> _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionGranted
                            false -> _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionDenied
                        }
                    }
                }
            }
        }
    }

    init {
        activity?.lifecycle?.addObserver(this)
        observeBluetoothAdapter()
    }

    actual fun askUserToEnableBluetoothIfNotOnAlready() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity?.let { startActivityForResult(it, enableBtIntent, 1002, null) }
            return
        }
        _bluetoothStateFlow.value = BluetoothState.EnabledAndPermissionGranted
    }

    actual fun askUserToGrantBluetoothPermissions() {
        val permissionManager = PermissionManager.INSTANCE
        permissionManager.requestPermissions(bluetoothPermissions)
        CoroutineScope(Dispatchers.Main).launch {
            permissionManager.grantedPermissions.collect { permissions ->
                if (bluetoothPermissions.any { !permissions.contains(it) }) {
                    _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionDenied
                    return@collect
                }
                _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionGranted
                askUserToEnableBluetoothIfNotOnAlready()
            }
        }
    }

    private fun observeBluetoothAdapter() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            _bluetoothStateFlow.value = BluetoothState.NotSupported
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            when (PermissionManager.INSTANCE.hasPermissions(bluetoothPermissions)) {
                true -> _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionGranted
                false -> _bluetoothStateFlow.value = BluetoothState.DisabledAndPermissionDenied
            }
        }
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        activity?.let { ContextCompat.registerReceiver(it, broadcastReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED) }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activity?.unregisterReceiver(broadcastReceiver)
        super.onDestroy(owner)
    }

    actual fun setBluetoothError(exception: Exception) {
        _bluetoothStateFlow.value = BluetoothState.Error(exception)
    }
}