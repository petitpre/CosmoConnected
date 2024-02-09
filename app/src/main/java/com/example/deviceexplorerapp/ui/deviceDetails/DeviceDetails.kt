package com.example.deviceexplorerapp.ui.deviceDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DeviceDetails(address: String, onNavBack: () -> Unit) {
    val viewModel: DeviceDetailsViewModel = koinViewModel { parametersOf(address) }
    val state by viewModel.state.collectAsState()

    DeviceDetailsContent(state, onNavBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceDetailsContent(state: DeviceDetailsViewModel.UiState, onNavBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                title = {
                    Text("Devices list")
                }
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).padding(8.dp)) {
            Text("Name : ${state.device?.name}")
            Text("address : ${state.device?.macAdress}")
            Text("version : ${state.device?.firmwareVersion}")
            Text("serial : ${state.device?.serial}")
        }
    }
}