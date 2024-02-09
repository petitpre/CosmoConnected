package com.example.deviceexplorerapp.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class DeviceRepositoryTest {

    @Test
    fun refreshDevicesList(): Unit = runBlocking {

        val deviceService = mock<DeviceService> {
            onBlocking { listDevices() } doReturn DeviceListDTO(
                listOf(
                    DeviceDTO(
                        "00:00:00:00:00",
                        "Testing"
                    )
                )
            )
        }
        val deviceDao = mock<DevicesDao> {
            onBlocking { refreshContent(any()) } doReturn Unit
        }

        val devicesRepository = DevicesRepository(deviceService, deviceDao)

        devicesRepository.refreshDevicesList()

        verify(deviceService).listDevices()
        verify(deviceDao).refreshContent(
            eq(
                listOf(
                    Device(
                        "00:00:00:00:00",
                        "Testing"
                    )
                )
            )
        )
    }


    @Test
    fun refreshDevicesListError(): Unit = runBlocking {

        val deviceService = mock<DeviceService> {
            onBlocking { listDevices() } doThrow RuntimeException("Network error")
        }
        val deviceDao = mock<DevicesDao> {
            onBlocking { refreshContent(any()) } doReturn Unit
        }

        runCatching {
            val devicesRepository = DevicesRepository(deviceService, deviceDao)
            devicesRepository.refreshDevicesList()
        }.onFailure {
            verify(deviceService).listDevices()
            verify(deviceDao, never()).refreshContent(emptyList())
        }.onSuccess {
            Assert.fail("should throw an exception")
        }

    }

}