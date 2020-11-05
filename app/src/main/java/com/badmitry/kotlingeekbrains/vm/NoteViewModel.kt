package com.badmitry.kotlingeekbrains.vm

import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.Color
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val repository: Repository) : BaseViewModel<Note?>(repository) {
    private val DATE_TIME_FORMAT = "dd.MM.yy HH:mm"
    private val onBackPressedChannel = Channel<Unit>()
    private val isTitleLessThreeChannel = Channel<Unit>()
    private val startDelDialogChannel = Channel<Unit>()
    private val showPaletteChannel = Channel<Boolean>()
    private val changeColorChannel = Channel<Color>()
    private val hideProgressBarChannel = Channel<Boolean>()
    private var pendingNote: Note? = null
    private var showPalette = false
    fun getStartDelDialogChannel() = startDelDialogChannel
    fun getOnBackPressedChannel() = onBackPressedChannel
    fun isTitleLessThreeChannel() = isTitleLessThreeChannel
    fun getShowPaletteChannel() = showPaletteChannel
    fun getChangeColorChannel() = changeColorChannel
    fun getHideProgressBarChannel() = hideProgressBarChannel

    suspend fun startDelDialog() {
        startDelDialogChannel.send(Unit)
    }

    suspend fun deleteNote() {
        try {
            pendingNote?.id?.let {
                launch {
                    repository.deleteNote(it)
                }
            }
            pendingNote = null
            onBackPressedChannel.send(Unit)
        } catch (e: Throwable) {
            setError(e)
        }
    }

    suspend fun togglePalette() {
        showPalette = !showPalette
        showPaletteChannel.send(showPalette)
    }

    suspend fun loadNote(id: String) = launch {
        try {
            repository.getNoteById(id).let {
                pendingNote = it
                setData(it)
                hideProgressBar()
            }
        } catch (e: Throwable) {
            setError(e)
        }
    }

    suspend fun hideProgressBar() {
            hideProgressBarChannel.send(true)
    }

    suspend fun onBackPressed() {
        val newTitle: String = pendingNote?.title ?: ""
        if (newTitle.length < 3) {
            isTitleLessThreeChannel.send(Unit)
            pendingNote = null
            return
        }
        onBackPressedChannel.send(Unit)
    }

    fun saveNote(title: String, text: String) {
        val date = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        pendingNote = pendingNote?.copy(
                title = title,
                notes = text,
                lastChanged = date
        ) ?: Note(UUID.randomUUID().toString(), title, text, lastChanged = date)
    }

    suspend fun changeNoteColor(color: Color) {
        val date = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault()).format(Date())
        pendingNote = pendingNote?.copy(
                color = color,
                lastChanged = date
        ) ?: Note(UUID.randomUUID().toString(), "", "", color, date)
        changeColorChannel.send(color)
    }

    public override fun onCleared() {
        pendingNote?.let {
            launch {
                repository.saveNote(it)
            }
        }
    }
}
