package com.example.deviceexplorerapp.ui.bleScanner

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanMode
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScannerSettings
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val BLE_SCAN_TIMEOUT = 5_000L

class BleScannerViewModel : ViewModel(), KoinComponent {
    private val context: Context by inject()

    data class UiState(
        val deviceList: List<ServerDevice> = emptyList(),
        val deviceProperties: Pair<ServerDevice, List<ClientBleGattCharacteristic>>? = null
    )

    private val mState = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = mState

    private var scanJob: Job? = null

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    suspend fun startScan(): Result<Unit> = runCatching {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            mState.update { it.copy(deviceList = emptyList(), deviceProperties = null) }

            val aggregator = BleScanResultAggregator()
            BleScanner(context).scan(
                settings = BleScannerSettings(
                    scanMode = BleScanMode.SCAN_MODE_LOW_LATENCY,
                    includeStoredBondedDevices = false
                )
            )
                .map { aggregator.aggregateDevices(it) }
                .collectLatest { newDevices ->
                    mState.update {
                        it.copy(deviceList = newDevices)
                    }
                }
        }
        delay(BLE_SCAN_TIMEOUT)
        scanJob?.cancel()
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT])
    suspend fun onConnect(device: ServerDevice): Result<Unit> =
        kotlin.runCatching {
            scanJob?.cancel()
            withTimeout(5_000) {
                mState.update { it.copy(deviceProperties = null) }

                Log.d("BleScannerViewModel", "Connecting to: ${device.address}")
                val client = ClientBleGatt.connect(context, device, viewModelScope)

                if (!client.isConnected) error("not connected to client")

                val services = client.discoverServices()
                val characteristic = services.services.flatMap {
                    Log.d("BleScannerViewModel", "service: ${it.uuid}")
                    it.characteristics
                }
                client.disconnect()

                mState.update { it.copy(deviceProperties = device to characteristic) }
            }
        }.onFailure {
            onHideContent()
        }

    fun onHideContent() {
        mState.update { it.copy(deviceProperties = null) }
    }
}