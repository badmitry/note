package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.ViewModel
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.error.NotAuthentication
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel<S>(private val repository: Repository) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }
    private val viewStateChannel = BroadcastChannel<S>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    fun getViewState(): ReceiveChannel<S> = viewStateChannel.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    protected fun setError(e: Throwable) = launch {
        errorChannel.send(e)
    }

    protected fun setData(data: S) = launch { viewStateChannel.send(data) }

    private val notAuthenticationChannel = Channel<Unit>(Channel.CONFLATED)
    private val showErrorChannel = Channel<String>(Channel.CONFLATED)
    private val notInternetConnectionChannel = Channel<Unit>(Channel.CONFLATED)
    fun getNotInternetConnectionChannel(): ReceiveChannel<Unit> = notInternetConnectionChannel
    fun getNotAuthenticationChannel(): ReceiveChannel<Unit> = notAuthenticationChannel
    fun getShowErrorChannel(): ReceiveChannel<String> = showErrorChannel
    suspend fun renderError(error: Throwable) {
        when (error) {
            is NotAuthentication -> {
                startAuthentication()
            }
            else -> error.message?.let { showErrorChannel.send(it) }
        }
    }

    suspend fun startAuthentication() {
        coroutineScope {
            launch(Dispatchers.IO) {
                if (!repository.checkInternetConnection()) {
                    notInternetConnectionChannel.send(Unit)
                } else {
                    notAuthenticationChannel.send(Unit)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
    }
}