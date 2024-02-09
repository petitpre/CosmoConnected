package com.example.deviceexplorerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DevicesDao {
    @Query("SELECT * from device")
    abstract fun listDevices(): Flow<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg devices: Device)

    @Query("DELETE FROM device")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun refreshContent(devices: List<Device>) {
        deleteAll()
        insert(*devices.toTypedArray())
    }

    @Query("SELECT * from device where macAdress = :address")
    abstract fun getDevice(address: String): Flow<Device>
}