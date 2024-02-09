package com.example.deviceexplorerapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.deviceexplorerapp.data.AppDatabase
import com.example.deviceexplorerapp.data.DeviceService
import com.example.deviceexplorerapp.data.DevicesRepository
import com.example.deviceexplorerapp.ui.bleScanner.BleScannerViewModel
import com.example.deviceexplorerapp.ui.deviceDetails.DeviceDetailsViewModel
import com.example.deviceexplorerapp.ui.deviceList.DeviceListViewModel
import com.example.deviceexplorerapp.ui.utils.PermissionViewModel
import com.squareup.moshi.Moshi
import no.nordicsemi.android.common.permissions.ble.bluetooth.BluetoothStateManager
import no.nordicsemi.android.common.permissions.ble.location.LocationStateManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ExplorerApps : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ExplorerApps)
            modules(explorerAppModule)
        }
    }
}

val explorerAppModule = module {
    factory {
        Moshi.Builder().build()
    }
    single {
        Room.databaseBuilder(
            get<Context>(),
            AppDatabase::class.java,
            "device_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().devicesDao() }

    single {
        Retrofit.Builder()
            .baseUrl("https://cosmo-api.develop-sr3snxi-x6u2x52ooksf4.de-2.platformsh.site")
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .client(
                OkHttpClient.Builder()
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
                    .build()
            )
            .build()
    }
    factory<DeviceService> {
        get<Retrofit>().create(DeviceService::class.java)
    }

    single {
        DevicesRepository(get(),get())
    }
    single {
        BluetoothStateManager(get())
    }
    single {
        LocationStateManager(get())
    }

    viewModel { DeviceListViewModel() }
    viewModel { parameters -> DeviceDetailsViewModel(parameters.get()) }
    viewModel { BleScannerViewModel() }

    viewModel { PermissionViewModel() }

}