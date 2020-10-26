package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainMenuViewModel : ViewModel() {
    private val startLogoutDialogLiveData = MutableLiveData<Unit>()
    private val logoutOkLiveData = MutableLiveData<Unit>()

    fun getStartLogoutDialogLiveData(): LiveData<Unit> = startLogoutDialogLiveData
    fun getLogoutOkLiveData(): LiveData<Unit> = logoutOkLiveData

    fun showLogoutDialog() {
        startLogoutDialogLiveData.value = Unit
    }

    fun logoutOk() {
        logoutOkLiveData.value = Unit
    }

}