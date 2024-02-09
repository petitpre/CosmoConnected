package com.example.deviceexplorerapp.ui.bleScanner

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.unit.dp
import com.example.deviceexplorerapp.ui.utils.BluetoothPermissionRequiredView
import com.example.deviceexplorerapp.ui.utils.RequireBluetooth
import com.example.deviceexplorerapp.ui.utils.Separator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import org.koin.androidx.compose.koinViewModel

@SuppressLint("MissingPermission")
@Composable
fun BleScanner(onNavBack: () -> Unit) {
    val viewModel: BleScannerViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    BleScannerContent(
        state,
        onNavBack = onNavBack,
        onStartScan = { viewModel.startScan() },
        onConnect = { viewModel.onConnect(it) },
        onHideContent = { viewModel.onHideContent() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BleScannerContent(
    state: BleScannerViewModel.UiState,
    onNavBack: () -> Unit,
    onStartScan: suspend () -> Result<Unit>,
    onConnect: suspend (ServerDevice) -> Result<Unit>,
    onHideContent: () -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(refreshState.isRefreshing) {
        if (refreshState.isRefreshing) {
            onStartScan().getOrElse {
                snackbarHostState
                    .showSnackbar(
                        message = "Error ${it.message}",
                        duration = SnackbarDuration.Short
                    )
            }
            refreshState.endRefresh()
        }
    }

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
                    Text("Ble scanner")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
            RequireBluetooth(
                contentWithoutBluetooth = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        BluetoothPermissionRequiredView()
                    } else {
                        Text("Bluetooth is required. Please enable it to continue.")
                    }
                }
            ) {
                LaunchedEffect("start_scan") {
                    refreshState.startRefresh()
                }

                LazyColumn(Modifier.fillMaxSize()) {

                    state.deviceList.forEach { device ->

                        val deviceProperties =
                            state.deviceProperties?.takeIf { it.first == device }?.second

                        item {
                            ServerDeviceListItem(
                                modifier = Modifier.clickable {
                                    if (deviceProperties == null) {
                                        scope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()

                                            val snackTask = async {
                                                snackbarHostState.showSnackbar(
                                                    message = "Connecting to ${device.name ?: device.address}...",
                                                    duration = SnackbarDuration.Indefinite
                                                )
                                            }

                                            onConnect(device).onFailure {
                                                snackTask.cancel()
                                                snackbarHostState
                                                    .showSnackbar(
                                                        message = "Error ${it.message}",
                                                        duration = SnackbarDuration.Short
                                                    )
                                            }.onSuccess {
                                                snackTask.cancel()
                                            }
                                        }
                                    } else {
                                        onHideContent()
                                    }
                                },
                                device = device,
                                expanded = deviceProperties != null
                            )
                        }

                        deviceProperties?.let { properties ->
                            items(properties) { prop ->
                                CharacteristicListItem(prop)
                            }
                        }

                        item {
                            Separator()
                        }
                    }
                }

                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = refreshState,
                )

            }
        }
    }
}


@Composable
private fun ServerDeviceListItem(
    modifier: Modifier = Modifier,
    device: ServerDevice,
    expanded: Boolean
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            Modifier
                .weight(1f)
                .padding(8.dp),

            ) {
            Text(text = device.name ?: "Unknown", style = MaterialTheme.typography.labelLarge)
            Text(text = device.address, style = MaterialTheme.typography.labelMedium)
        }
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "expand"
        )
    }
}

@Composable
private fun CharacteristicListItem(prop: ClientBleGattCharacteristic) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            text = prop.uuid.toString(),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
