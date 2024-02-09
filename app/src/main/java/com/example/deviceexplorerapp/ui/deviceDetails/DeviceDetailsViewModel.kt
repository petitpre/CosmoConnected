package com.example.deviceexplorerapp.ui.deviceDetails

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

class DeviceDetailsViewModel(address: String) : ViewModel(), KoinComponent {

    private val devicesRepository: DevicesRepository by inject()

    data class UiState(
        val device: Device? = null
    )

    private val mState = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = mState

    init {
        viewModelScope.launch {
            devicesRepository.searchDevice(address).collectLatest { device ->
                mState.update {
                    it.copy(device = device)
                }
            }
        }
    }

}