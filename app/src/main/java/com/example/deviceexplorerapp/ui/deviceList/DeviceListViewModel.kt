package com.example.deviceexplorerapp.ui.deviceList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deviceexplorerapp.data.Device
import com.example.deviceexplorerapp.data.DevicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DeviceListViewModel : ViewModel(), KoinComponent {

    private val devicesRepository: DevicesRepository by inject()

    data class UiState(
        val deviceList: List<Device> = emptyList()
    )

    private val mState = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = mState

    init {
        viewModelScope.launch {
            devicesRepository.devices.collectLatest { newDeviceList ->
                mState.update {
                    it.copy(deviceList = newDeviceList)
                }
            }
        }
    }

    suspend fun refreshContent(): Result<Unit> = runCatching {
//        delay(1000)
//        if (Math.random() > .5) error("something gone wrong")
        devicesRepository.refreshDevicesList()
    }

}