package com.example.deviceexplorerapp.data

import com.squareup.moshi.JsonClass
import retrofit2.http.GET


interface DeviceService {

    @GET("/test/devices")
    suspend fun listDevices(): DeviceListDTO

}

@JsonClass(generateAdapter = true)
data class DeviceListDTO(
    val devices: List<DeviceDTO>
)

@JsonClass(generateAdapter = true)
data class DeviceDTO(
    val macAddress: String,
    val model: String,
    val product: String = "",
    val firmwareVersion: String = "",
    val serial: String = "",
    val installationMode: String = "unknown",
    val brakeLight: Boolean = false,
    val lightMode: String = "none",
    val lightAuto: Boolean = false,
    val lightValue: Int = 0
)