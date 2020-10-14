package com.badmitry.kotlingeekbrains.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.ui.note.NoteActivity
import com.badmitry.kotlingeekbrains.vm.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        rv_notes.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter() {
            NoteActivity.startNoteActivity(this, it)
        }
        rv_notes.adapter = adapter

        viewModel.viewState().observe(this, {value ->
            value?.let {adapter.notes = it.notes}
        })

        button_add.setOnClickListener {
            NoteActivity.startNoteActivity(this)
        }
    }
}