package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.ui.note.NoteViewState
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel : BaseViewModel<Note?, NoteViewState>() {
    private val DATE_TIME_FORMAT = "dd.MM.yy HH:mm"
    private val onBackPressedLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val lengthTitleLessThreeLiveData: MutableLiveData<Unit> = MutableLiveData()
    private var result: LiveData<NoteResult>? = null
    private val observer = object : Observer<NoteResult> {
        override fun onChanged(t: NoteResult?) {
            when (t) {
                is NoteResult.Success<*> -> {
                    val note = t.data as? Note
                    viewStateLiveData.value = NoteViewState(note)
                    pendingNote = note
                }
                is NoteResult.Error -> viewStateLiveData.value = NoteViewState(error = t.error)
            }
            result?.removeObserver(this)
        }
    }

    var pendingNote: Note? = null

    init {
        viewStateLiveData.value = NoteViewState()
    }

    fun setOnDelButtonClicker() {
        pendingNote?.let {
            Repository.deleteNote(it)
            pendingNote = null
        }
        onBackPressedLiveData.value = Unit
    }

    fun loadNote(id: String) {
        result = Repository.getNoteById(id)
        result.let {
            it?.observeForever(observer)
        }
    }

    fun getLiveDataOnBackPressed(): LiveData<Unit> = onBackPressedLiveData

    fun getLiveDataIfTitleLessThree(): LiveData<Unit> = lengthTitleLessThreeLiveData

    fun onBackPressed() {
        val newTitle: String = pendingNote?.title ?: ""
        if (newTitle.length < 3) {
            lengthTitleLessThreeLiveData.value = Unit
            return
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

    override fun onCleared() {
        result.let {
            it?.removeObserver(observer)
        }
        pendingNote?.let {
            Repository.saveNote(it)
        }
    }
}
