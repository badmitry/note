package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.User
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.ui.splash.SplashViewState

class SplashViewModel : BaseViewModel<Boolean?, SplashViewState>() {
    private var userLiveData: MutableLiveData<User> = Repository.getCurrentUser() as MutableLiveData<User>
    private val startMainActivityLiveData: MutableLiveData<Unit> = MutableLiveData()
    fun getStartMainActivityLiveData(): LiveData<Unit> = startMainActivityLiveData

    private val observer = object : Observer<User> {
        override fun onChanged(t: User?) {
            t?.let { viewStateLiveData.value = SplashViewState(true) }
                    ?: let {
                        viewStateLiveData.value = SplashViewState(error = NotAuthentication())
                    }
            userLiveData.removeObserver(this)
        }
    }

    fun startMainActivity() {
        startMainActivityLiveData.value = Unit
    }

    fun requestUser() {
        userLiveData = Repository.getCurrentUser() as MutableLiveData<User>
        userLiveData.observeForever(observer)
    }

    override fun onCleared() {
        userLiveData.removeObserver(observer)
    }
}