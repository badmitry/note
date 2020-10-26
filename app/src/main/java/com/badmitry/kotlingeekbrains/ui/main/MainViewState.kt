package com.badmitry.kotlingeekbrains.ui.main

import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.ui.BaseViewState

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) : BaseViewState<List<Note>?>(notes, error)