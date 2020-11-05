package com.badmitry.kotlingeekbrains.vm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.badmitry.kotlingeekbrains.data.Repository
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.model.NoteResult
import com.badmitry.kotlingeekbrains.ui.note.NoteViewState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteViewModelTest{
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<Repository>()
    private val noteLiveData = MutableLiveData<NoteResult>()
    private val testNote = Note("1", "title1", "text1")

    private lateinit var viewModel: NoteViewModel

//    @Before
//    fun setup() {
//        clearAllMocks()
//        every { mockRepository.getNoteById(testNote.id) } returns noteLiveData
//        every { mockRepository.deleteNote(testNote.id) } returns noteLiveData
//        every { mockRepository.saveNote(any()) } returns noteLiveData
//        viewModel = NoteViewModel(mockRepository)
//    }
//
//    @Test
//    fun `loadNote should return NoteViewState data` () {
//        var result: NoteViewState? = null
//        val testData = NoteViewState(testNote, null)
//        viewModel.viewStateLiveData.observeForever {
//            result = it
//        }
//        viewModel.loadNote(testNote.id)
//        noteLiveData.value = NoteResult.Success(testNote)
//        assertEquals(testData.data?.id, result?.data?.id)
//        assertEquals(testData.data?.title, result?.data?.title)
//        assertEquals(testData.data?.notes, result?.data?.notes)
//        assertEquals(testData.data?.color, result?.data?.color)
//        assertEquals(viewModel.pendingNote, result?.data)
//    }
//
//    @Test
//    fun `loadNote should return error`() {
//        var result: Throwable? = null
//        val testData = Throwable("error")
//        viewModel.viewStateLiveData.observeForever {
//            result = it.error
//        }
//        viewModel.loadNote(testNote.id)
//        noteLiveData.value = NoteResult.Error(testData)
//        assertEquals(testData, result)
//    }
//
//    @Test
//    fun `deleteNote should make pendingNote null` () {
//        var result:Note? = null
//        viewModel.viewStateLiveData.observeForever {
//            result = it.data
//        }
//        viewModel.loadNote(testNote.id)
//        noteLiveData.value = NoteResult.Success(testNote)
//
//        viewModel.deleteNote()
//        assertEquals(viewModel.pendingNote, null)
//    }
//
//    @Test
//    fun `note should save once`() {
//        viewModel.loadNote(testNote.id)
//        noteLiveData.value = NoteResult.Success(testNote)
//
//        viewModel.onCleared()
//        verify (exactly = 1) { mockRepository.saveNote(testNote) }
//    }
}


