package com.example.deviceexplorerapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deviceexplorerapp.ui.bleScanner.BleScanner
import com.example.deviceexplorerapp.ui.deviceDetails.DeviceDetails
import com.example.deviceexplorerapp.ui.deviceList.DeviceList

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "devices"
    ) {
        composable("devices") {
            DeviceList(onNavigateToBleScanner = {
                navController.navigate("scanner")
            },
                onNavigateToDevice = {
                    navController.navigate("devices/${it.macAdress}")
                })
        }

        composable("devices/{macAdress}") {
            DeviceDetails(address = it.arguments?.getString("macAdress")!!) {
                navController.popBackStack()
            }
        }

        composable("scanner") {
            BleScanner {
                navController.popBackStack()
            }
        }
    }
}
