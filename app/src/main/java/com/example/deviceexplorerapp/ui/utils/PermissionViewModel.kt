package com.example.deviceexplorerapp.ui.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.common.permissions.ble.bluetooth.BluetoothStateManager
import no.nordicsemi.android.common.permissions.ble.location.LocationStateManager
import no.nordicsemi.android.common.permissions.ble.util.BlePermissionNotAvailableReason
import no.nordicsemi.android.common.permissions.ble.util.BlePermissionState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * Ugly copy/paste from nordic code to allow quick handling of BLE permission
 */

class PermissionViewModel : ViewModel(), KoinComponent {
    private val bluetoothManager: BluetoothStateManager by inject()
    private val locationManager: LocationStateManager by inject()

    val bluetoothState = bluetoothManager.bluetoothState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            BlePermissionState.NotAvailable(BlePermissionNotAvailableReason.NOT_AVAILABLE)
        )

    val locationPermission = locationManager.locationState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            BlePermissionState.NotAvailable(BlePermissionNotAvailableReason.NOT_AVAILABLE)
        )

    fun refreshBluetoothPermission() {
        bluetoothManager.refreshPermission()
    }

    fun refreshLocationPermission() {
        locationManager.refreshPermission()
    }

    fun markLocationPermissionRequested() {
        locationManager.markLocationPermissionRequested()
    }

    fun markBluetoothPermissionRequested() {
        bluetoothManager.markBluetoothPermissionRequested()
    }

    fun isBluetoothScanPermissionDeniedForever(context: Context): Boolean {
        return bluetoothManager.isBluetoothScanPermissionDeniedForever(context)
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return locationManager.isLocationPermissionDeniedForever(context)
    }
}
