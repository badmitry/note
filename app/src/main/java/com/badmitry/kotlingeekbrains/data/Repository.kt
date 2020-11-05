package com.badmitry.kotlingeekbrains.data

import com.badmitry.kotlingeekbrains.data.provider.DataProvider
import com.badmitry.kotlingeekbrains.data.entity.Note

class Repository(private val dataProvider: DataProvider) {

    suspend fun subscribeNotes() = dataProvider.subscribeNotes()
    suspend fun saveNote(note: Note) = dataProvider.saveNote(note)
    suspend fun getNoteById(id: String) = dataProvider.getNoteById(id)
    suspend fun deleteNote(id: String) = dataProvider.deleteNote(id)
    suspend fun getCurrentUser() = dataProvider.getCurrentUser()
    suspend fun checkInternetConnection()= dataProvider.checkInternetConnection()
}