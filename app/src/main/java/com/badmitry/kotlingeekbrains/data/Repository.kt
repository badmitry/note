package com.badmitry.kotlingeekbrains.data

import com.badmitry.kotlingeekbrains.data.provider.DataProvider
import com.badmitry.kotlingeekbrains.data.provider.FirebaseDataProvider
import com.badmitry.kotlingeekbrains.data.entity.Note

object Repository {

    val dataProvider: DataProvider = FirebaseDataProvider()

    fun getNotes() = dataProvider.getNotes()
    fun saveNote(note: Note) = dataProvider.saveNote(note)
    fun getNoteById(id: String) = dataProvider.getNoteById(id)
    fun deleteNote(note: Note) = dataProvider.deleteNote(note)
    fun getCurrentUser() = dataProvider.getCurrentUser()
}