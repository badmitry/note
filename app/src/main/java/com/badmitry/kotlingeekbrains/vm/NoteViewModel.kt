package com.badmitry.kotlingeekbrains.vm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.ui.note.NoteViewState
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel (private val repository: Repository) : BaseViewModel<Note?, NoteViewState>(repository) {
    private val DATE_TIME_FORMAT = "dd.MM.yy HH:mm"
    private val onBackPressedLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val lengthTitleLessThreeLiveData: MutableLiveData<Unit> = MutableLiveData()
    private val startDelDialogLiveData = MutableLiveData<Unit>()
    private val showPaletteLiveData = MutableLiveData<Boolean>()
    private val changeColorLiveData = MutableLiveData<Color>()
    private val showProgressBarLiveData = MutableLiveData<Unit>()
    private var result: LiveData<NoteResult>? = null
    var pendingNote: Note? = null
    fun getStartDilDialogLiveData(): LiveData<Unit> = startDelDialogLiveData
    fun getLiveDataOnBackPressed(): LiveData<Unit> = onBackPressedLiveData
    fun getLiveDataIfTitleLessThree(): LiveData<Unit> = lengthTitleLessThreeLiveData
    fun getShowPaletteLiveData(): LiveData<Boolean> = showPaletteLiveData
    fun getChangeColorLiveData(): LiveData<Color> = changeColorLiveData
    fun getShowProgressBarLiveData(): LiveData<Unit> = showProgressBarLiveData

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
            showProgressBar()
            result?.removeObserver(this)
        }
    }

    init {
        viewStateLiveData.value = NoteViewState()
    }

    fun startDelDialog() {
        startDelDialogLiveData.value = Unit
    }

    fun deleteNote() {
        pendingNote?.id?.let {
            repository.deleteNote(it)
        }
        pendingNote = null
        onBackPressedLiveData.value = Unit
    }

    fun togglePalette() {
        showPaletteLiveData.value = showPaletteLiveData.value == false
    }

    fun loadNote(id: String) {
        result = repository.getNoteById(id)
        result?.observeForever(observer)
    }

    fun showProgressBar() {
        showProgressBarLiveData.value = Unit
    }

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

    fun changeNoteColor(color: Color) {
        val date = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        pendingNote = pendingNote?.copy(
                color = color,
                lastChanged = date
        ) ?: Note(UUID.randomUUID().toString(), "", "", lastChanged = date)
        changeColorLiveData.value = color
    }

    @VisibleForTesting
    public override fun onCleared() {
        result.let {
            it?.removeObserver(observer)
        }
        pendingNote?.let {
            repository.saveNote(it)
        }
    }
}
