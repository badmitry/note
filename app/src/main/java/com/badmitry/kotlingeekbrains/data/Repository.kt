package com.badmitry.kotlingeekbrains.data

import com.badmitry.kotlingeekbrains.data.provider.DataProvider
import com.badmitry.kotlingeekbrains.data.entity.Note

class Repository(private val dataProvider: DataProvider) {

    fun getNotes() = dataProvider.getNotes()
    fun saveNote(note: Note) = dataProvider.saveNote(note)
    fun getNoteById(id: String) = dataProvider.getNoteById(id)
    fun deleteNote(id: String) = dataProvider.deleteNote(id)
    fun getCurrentUser() = dataProvider.getCurrentUser()
    fun checkInternetConnection(): Boolean = dataProvider.checkInternetConnection()
}