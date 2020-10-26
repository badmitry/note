package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import com.badmitry.kotlingeekbrains.ui.BaseViewState

open class BaseViewModel<T, S : BaseViewState<T>> : ViewModel() {
    open val viewStateLiveData = MutableLiveData<S>()
    open fun getViewState(): LiveData<S> {
        return viewStateLiveData
    }
    open val notAuthenticationLiveData: MutableLiveData<Unit> = MutableLiveData()
    open val showErrorLiveData: MutableLiveData<String> = MutableLiveData()
    fun getNotAuthenticationLiveData() : LiveData<Unit> = notAuthenticationLiveData
    fun getShowErrorLiveData() : LiveData<String> = showErrorLiveData
    open fun renderError(error: Throwable) {
        when (error) {
            is NotAuthentication -> notAuthenticationLiveData.value = Unit
            else -> error.message?.let { showErrorLiveData.value = it }
        }
    }
}