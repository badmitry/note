package com.badmitry.kotlingeekbrains.ui.note

import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.ui.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) : BaseViewState<Note?>(note, error)