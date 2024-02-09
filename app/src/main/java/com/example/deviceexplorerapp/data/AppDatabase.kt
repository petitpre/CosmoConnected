package com.example.deviceexplorerapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Device::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun devicesDao(): DevicesDao
}