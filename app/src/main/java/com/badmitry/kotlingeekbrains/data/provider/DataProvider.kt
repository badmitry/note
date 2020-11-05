package com.badmitry.kotlingeekbrains.data.provider

import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.entity.User
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import kotlinx.coroutines.channels.ReceiveChannel

interface DataProvider {
    suspend fun subscribeNotes(): ReceiveChannel<NoteResult>
    suspend fun saveNote(note: Note)
    suspend fun getNoteById(id: String): Note
    suspend fun deleteNote(id: String)
    suspend fun getCurrentUser(): User?
    suspend fun checkInternetConnection(): Boolean
}