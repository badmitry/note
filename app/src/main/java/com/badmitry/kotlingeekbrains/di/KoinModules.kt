package com.badmitry.kotlingeekbrains.di

import com.badmitry.kotlingeekbrains.data.CheckerInternetConnection
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.provider.DataProvider
import com.badmitry.kotlingeekbrains.data.provider.FirebaseDataProvider
import com.badmitry.kotlingeekbrains.vm.MainViewModel
import com.badmitry.kotlingeekbrains.vm.NoteViewModel
import com.badmitry.kotlingeekbrains.vm.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { CheckerInternetConnection(get()) }
    single { FirebaseDataProvider(get(), get(), get()) } bind DataProvider::class
    single { Repository (get())}
}

val splashModule = module {
    viewModel  { SplashViewModel(get()) }
}

val mainModule = module {
    viewModel  { MainViewModel(get()) }
}

val noteModule = module {
    viewModel  { NoteViewModel(get())}
}