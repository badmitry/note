package com.badmitry.kotlingeekbrains.vm

import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@OptIn(ObsoleteCoroutinesApi::class)
class MainViewModel(repository: Repository) : BaseViewModel<List<Note>?>(repository) {

    private val startNoteActivityChannel = Channel<Note?>()
    private lateinit var notesChannel: ReceiveChannel<NoteResult>

    fun getStartNoteActivityChannel(): Channel<Note?> = startNoteActivityChannel

    fun startNoteActivity(note: Note?) {
        launch {
            startNoteActivityChannel.send(note)
        }
    }

    init {
        launch {
            notesChannel = repository.subscribeNotes()
            notesChannel.consumeEach {
                when (it) {
                    is NoteResult.Success<*> -> setData(it.data as? List<Note>)
                    is NoteResult.Error -> setError(it.error)
                }
            }
        }
    }

    public override fun onCleared() {
        super.onCleared()
        notesChannel.cancel()
        startNoteActivityChannel.cancel()
    }
}