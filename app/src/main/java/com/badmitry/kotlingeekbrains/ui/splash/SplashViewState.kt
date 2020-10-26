package com.badmitry.kotlingeekbrains.ui.splash

import com.badmitry.kotlingeekbrains.ui.BaseViewState

class SplashViewState(authenticated: Boolean? = null, error: Throwable? = null) : BaseViewState<Boolean?>(authenticated, error)