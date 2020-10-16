package com.badmitry.kotlingeekbrains.vm

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.model.Note
import com.badmitry.kotlingeekbrains.ui.App
import com.badmitry.kotlingeekbrains.ui.main.MainViewState
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel : ViewModel() {
    private val DATE_TIME_FORMAT = "dd.MM.yy HH:mm"
    private val onBackPressedLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val lengthTitleLessThreeLiveData: MutableLiveData<Unit> = MutableLiveData()
    var pendingNote: Note? = null

    override fun onCleared() {
        pendingNote?.let {
            Repository.saveNote(it)
        }
    }

    fun getLiveDataOnBackPressed(): LiveData<Unit> = onBackPressedLiveData

    fun getLiveDataIfTitleLessThree(): LiveData<Unit> = lengthTitleLessThreeLiveData

    fun onBackPressed() {
        val newTitle: String = pendingNote?.title ?: ""
        if (newTitle.length < 3) {
            lengthTitleLessThreeLiveData.value = Unit
        }
        onBackPressedLiveData.value = Unit
    }

    fun saveNote(title: String, text: String) {
        val date = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        pendingNote = pendingNote?.copy(
                title = title,
                notes = text,
                lastChanged = date
        ) ?: Note(UUID.randomUUID().toString(), title, text, lastChanged = date)
    }
}
