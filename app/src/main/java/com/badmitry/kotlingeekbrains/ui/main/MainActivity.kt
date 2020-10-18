package com.badmitry.kotlingeekbrains.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.model.Note
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.ui.note.NoteActivity
import com.badmitry.kotlingeekbrains.vm.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<List<Note>?, MainViewState>() {

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    override val layoutRes = R.layout.activity_main
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rv_notes.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter() {
            NoteActivity.startNoteActivity(this, it.id)
        }
        rv_notes.adapter = adapter
        button_add.setOnClickListener {
            viewModel.setOnAddButtonClicker()
        }
        viewModel.getLiveDataOnButtonAddPressed().observe(this, { value ->
            NoteActivity.startNoteActivity(this)
        })
    }

    override fun renderData(data: List<Note>?) {
        data?.let { adapter.notes = it }
    }
}