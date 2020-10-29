package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.ui.BaseViewState
import kotlin.concurrent.thread

open class BaseViewModel<T, S : BaseViewState<T>>(private val repository: Repository) : ViewModel() {
    open val viewStateLiveData = MutableLiveData<S>()
    open fun getViewState(): LiveData<S> {
        return viewStateLiveData
    }

    open val notAuthenticationLiveData: MutableLiveData<Unit> = MutableLiveData()
    open val showErrorLiveData: MutableLiveData<String> = MutableLiveData()
    private val notInternetConnectionLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val isReconnectionLiveData: MutableLiveData<Boolean> = MutableLiveData()
    fun getNotInternetConnectionLiveData(): LiveData<Unit> = notInternetConnectionLiveData
    fun getNotAuthenticationLiveData(): LiveData<Unit> = notAuthenticationLiveData
    fun getShowErrorLiveData(): LiveData<String> = showErrorLiveData
    fun getIsReconnectionLiveData(): LiveData<Boolean> = isReconnectionLiveData
    open fun renderError(error: Throwable) {
        when (error) {
            is NotAuthentication -> {
                thread {
                    if (!repository.checkInternetConnection()) {
                        notInternetConnectionLiveData.postValue(Unit)
                    } else {
                        notAuthenticationLiveData.postValue(Unit)
                    }
                }
            }
            else -> error.message?.let { showErrorLiveData.value = it }
        }
    }

    fun reconnectionToInternet(boolean: Boolean) {
            isReconnectionLiveData.value = boolean
    }
}