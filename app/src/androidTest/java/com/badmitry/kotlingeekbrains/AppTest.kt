package com.badmitry.kotlingeekbrains

import android.app.Application
import org.koin.android.ext.android.startKoin

class AppTest : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf())
    }
}