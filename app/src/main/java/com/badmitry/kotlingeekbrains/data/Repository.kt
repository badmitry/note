package com.badmitry.kotlingeekbrains.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.data.model.Note
import java.util.*

object Repository {

    private val notesLiveData = MutableLiveData<List<Note>>()

    private val notes: MutableList<Note> = mutableListOf(
            Note(UUID.randomUUID().toString(), "Моя первая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.WHITE),
            Note(UUID.randomUUID().toString(), "Моя вторая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.BLUE),
            Note(UUID.randomUUID().toString(), "Моя третья заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.GREEN),
            Note(UUID.randomUUID().toString(), "Моя четвертая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.PINK),
            Note(UUID.randomUUID().toString(), "Моя пятая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.RED),
            Note(UUID.randomUUID().toString(), "Моя шестая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.VIOLET),
            Note(UUID.randomUUID().toString(), "Моя седьмая заметка",
                    "Kotlin очень краткий, но при этом выразительный язык",
                    Color.YELLOW)
    )

    init {
        notesLiveData.value = notes
    }

    fun getNotes(): LiveData<List<Note>> {
        return notesLiveData
    }


    fun saveNote(note: Note) {
        addOrReplace(note)
        notesLiveData.value = notes
    }

    private fun addOrReplace(note: Note) {
        for (i in notes.indices) {
            if (notes[i] == note) {
                notes[i] = note
                return
            }
        }
        notes.add(note)
    }
}