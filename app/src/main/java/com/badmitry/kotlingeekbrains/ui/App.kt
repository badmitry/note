package com.badmitry.kotlingeekbrains.ui

import android.app.Application
import com.badmitry.kotlingeekbrains.di.*
import org.koin.android.ext.android.startKoin

class App: Application(){
    companion object {
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin(this, listOf(appModule, splashModule, mainModule, noteModule))
    }
}