package com.example.deviceexplorerapp.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DevicesRepository(
    private val deviceService: DeviceService,
    private val devicesDao: DevicesDao
) {

    val devices: Flow<List<Device>> by lazy { devicesDao.listDevices() }

    suspend fun refreshDevicesList() = withContext(Dispatchers.IO) {
        val devices = deviceService.listDevices().devices.map { it.toDevice() }
        devicesDao.refreshContent(devices)
    }

    fun searchDevice(address: String): Flow<Device> {
        return devicesDao.getDevice(address)
    }

}

private fun DeviceDTO.toDevice(): Device {
    return Device(
        name = model, macAdress = macAddress, firmwareVersion = firmwareVersion, serial = serial
    )
}

@Entity(tableName = "device")
data class Device(
    @PrimaryKey val macAdress: String,
    val name: String,
    val firmwareVersion: String = "",
    val serial: String = ""
)

