package com.example.deviceexplorerapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import no.nordicsemi.android.common.permissions.ble.util.BlePermissionNotAvailableReason
import no.nordicsemi.android.common.permissions.ble.util.BlePermissionState
import org.koin.androidx.compose.koinViewModel


/**
 * Ugly copy/paste from nordic code to allow quick handling of BLE permission
 */

@Composable
fun RequireBluetooth(
    onChanged: (Boolean) -> Unit = {},
    contentWithoutBluetooth: @Composable (BlePermissionNotAvailableReason) -> Unit,
    content: @Composable () -> Unit,
) {
    val viewModel: PermissionViewModel = koinViewModel()
    val state by viewModel.bluetoothState.collectAsState()

    LaunchedEffect(state) {
        onChanged(state is BlePermissionState.Available)
    }

    when (val s = state) {
        BlePermissionState.Available -> content()
        is BlePermissionState.NotAvailable -> contentWithoutBluetooth(s.reason)
    }
}
