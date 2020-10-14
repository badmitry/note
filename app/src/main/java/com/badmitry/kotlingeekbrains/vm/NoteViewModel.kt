package com.badmitry.kotlingeekbrains.vm

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.model.Note
import com.badmitry.kotlingeekbrains.ui.App
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel : ViewModel() {
    private val DATE_TIME_FORMAT = "dd.MM.yy HH:mm"
    var pendingNote: Note? = null

    override fun onCleared() {
        pendingNote?.let {
            Repository.saveNote(it)
        }
    }

    fun saveNote(title: String, text: String) {
        if (title.length < 3) {
            Toast.makeText(App.instance?.applicationContext,"Заголовок должен содержать не менее 3 символов!", Toast.LENGTH_SHORT).show()
            return
        }
        val date = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        pendingNote = pendingNote?.copy(
                title = title,
                notes = text,
                lastChanged = date
        ) ?: Note(UUID.randomUUID().toString(), title, text, lastChanged = date)
    }
}
