package com.badmitry.kotlingeekbrains.data.provider

import androidx.lifecycle.LiveData
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.entity.User
import com.badmitry.kotlingeekbrains.data.model.NoteResult

interface DataProvider {
    fun getNotes(): LiveData<NoteResult>
    fun saveNote(note: Note): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun deleteNote(note: Note): LiveData<NoteResult>
    fun getCurrentUser(): LiveData<User>
}