package com.example.ondevice

import android.app.Application
import com.example.database.objectbox.ObjectBoxStore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OnDeviceApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBoxStore.init(applicationContext)
    }
}