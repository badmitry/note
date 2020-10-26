package com.badmitry.kotlingeekbrains.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.getColorInt
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.vm.NoteViewModel
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.activity_note.toolbar

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private const val EXTRA_NOTE = "note"
        fun startNoteActivity(context: Context, id: String? = null) = Intent(context, NoteActivity::class.java).apply {
            putExtra(EXTRA_NOTE, id)
            context.startActivity(this)
        }
    }

    override val viewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(NoteViewModel::class.java)
    }
    override val layoutRes = R.layout.activity_note
    private var note: Note? = null


    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) = viewModel.saveNote(field_title.text.toString(), field_body.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        } ?: let {
            supportActionBar?.title = getString(R.string.new_note)
        }
        viewModel.getLiveDataOnBackPressed().observe(this, { this.onBackPressed() })
        viewModel.getLiveDataIfTitleLessThree().observe(this, {
            this.onBackPressed()
            Toast.makeText(this, "Заголовок должен содержать не менее 3 символов!", Toast.LENGTH_SHORT).show()
        })
        button_del.setOnClickListener {
            viewModel.setOnDelButtonClicker()
        }
    }

    private fun initView() {
        field_body.removeTextChangedListener(textChangeListener)
        field_title.removeTextChangedListener(textChangeListener)
        note?.let {
            field_title.setText(it.title)
            field_body.setText(it.notes)
            toolbar.setBackgroundColor(it.color.getColorInt(this@NoteActivity))
        }

        field_title.addTextChangedListener(textChangeListener)
        field_body.addTextChangedListener(textChangeListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            viewModel.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun renderData(data: Note?) {
        this.note = data
        supportActionBar?.title = note?.lastChanged ?: getString(R.string.new_note)
        initView()
    }
}
