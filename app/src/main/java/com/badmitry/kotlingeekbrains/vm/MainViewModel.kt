package com.badmitry.kotlingeekbrains.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.ui.main.MainViewState

class MainViewModel : BaseViewModel<List<Note>?, MainViewState>() {

    private val notesObserver = Observer { result: NoteResult? ->
        result ?: return@Observer
        when(result) {
            is NoteResult.Success<*> -> viewStateLiveData.value = MainViewState(result.data as List<Note>)
            is NoteResult.Error -> viewStateLiveData.value = MainViewState(error = result.error)
        }
    }

    private val onAddButtonClickLiveData: MutableLiveData<Unit> = MutableLiveData()

    fun setOnAddButtonClicker() {
        onAddButtonClickLiveData.value = Unit
    }

    private val repository = Repository.getNotes()

    init {
        repository.observeForever(notesObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeObserver(notesObserver)
    }

    fun getLiveDataOnButtonAddPressed(): LiveData<Unit> = onAddButtonClickLiveData

}