package com.example.deviceexplorerapp.ui.deviceList

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deviceexplorerapp.data.Device
import com.example.deviceexplorerapp.ui.theme.DeviceExplorerAppTheme
import com.example.deviceexplorerapp.ui.utils.Separator
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeviceExplorerAppTheme {
        DeviceListContent(
            DeviceListViewModel.UiState(
                deviceList = listOf(Device(name = "test", macAdress = "00:00:00:00:00:00"))
            ), {}, {}, { runCatching { } }
        )
    }
}

@Composable
fun DeviceList(onNavigateToDevice: (Device) -> Unit, onNavigateToBleScanner: () -> Unit) {
    val viewModel: DeviceListViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    DeviceListContent(
        state = state,
        onDeviceSelected = onNavigateToDevice,
        onScanSelected = onNavigateToBleScanner,
        onRefresh = { viewModel.refreshContent() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceListContent(
    state: DeviceListViewModel.UiState,
    onDeviceSelected: (Device) -> Unit,
    onScanSelected: () -> Unit,
    onRefresh: suspend () -> Result<Unit>
) {
    val refreshState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect("init") {
        refreshState.startRefresh()
    }
    LaunchedEffect(refreshState.isRefreshing) {
        if (refreshState.isRefreshing) {
            Log.d("DeviceList", "Start refreshing content")
            onRefresh().onFailure {
                Log.w("DeviceList", it.message, it)
                snackbarHostState
                    .showSnackbar(
                        message = "Error ${it.message}",
                        duration = SnackbarDuration.Short
                    )
                refreshState.endRefresh()
            }.onSuccess {
                Log.w("DeviceList", "Refresh done")
                refreshState.endRefresh()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Devices list")
                },
                actions = {
                    IconButton(onClick = onScanSelected) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.BluetoothSearching,
                            contentDescription = "Ble Scanner"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }) { padding ->
        Box(
            Modifier
                .padding(padding)
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.deviceList) { device ->
                    DeviceListItem(
                        modifier = Modifier.clickable { onDeviceSelected(device) },
                        device = device
                    )
                }
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = refreshState,
            )
        }
    }
}

@Composable
private fun DeviceListItem(
    modifier: Modifier = Modifier,
    device: Device
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = device.name, style = MaterialTheme.typography.labelLarge)
        Text(text = device.macAdress, style = MaterialTheme.typography.labelMedium)
    }
    Separator()
}
