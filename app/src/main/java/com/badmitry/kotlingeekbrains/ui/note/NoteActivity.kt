package com.badmitry.kotlingeekbrains.ui.note

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.data.getColorInt
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.vm.NoteViewModel
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.activity_note.toolbar
import org.koin.android.viewmodel.ext.android.viewModel

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private const val EXTRA_NOTE = "note"
        fun startNoteActivity(context: Context, id: String? = null) = Intent(context, NoteActivity::class.java).apply {
            putExtra(EXTRA_NOTE, id)
            context.startActivity(this)
        }
    }

    private var noteId: String? = null

    override val viewModel: NoteViewModel by viewModel()
    override val layoutRes = R.layout.activity_note
    private var note: Note? = null


    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) = viewModel.saveNote(field_title.text.toString(), field_body.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel.getShowProgressBarLiveData().observe(this, { hideProgressBar() })
        noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            viewModel.loadNote(it)
        } ?: let {
            supportActionBar?.title = getString(R.string.new_note)
            viewModel.showProgressBar()
        }
        viewModel.getLiveDataOnBackPressed().observe(this, { this.onBackPressed() })
        viewModel.getLiveDataIfTitleLessThree().observe(this, {
            this.onBackPressed()
            Toast.makeText(this, "Заголовок должен содержать не менее 3 символов!", Toast.LENGTH_SHORT).show()
        })
        viewModel.getStartDilDialogLiveData().observe(this, { this.startDelDialog() })
        colorPicker.onColorClickListener = {
            viewModel.changeNoteColor(it)
        }
        viewModel.getShowPaletteLiveData().observe(this, {
            if (it) {
                colorPicker.close()
            } else {
                colorPicker.open()
            }
        })
        viewModel.getChangeColorLiveData().observe(this, {
            changeBackgroundColor(it)
        })
    }

    private fun startDelDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.delete_menu_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.del_ok) { dialog, which ->
                    viewModel.deleteNote(noteId)
                }
                .setNegativeButton(R.string.logout_cancel) { dialog, which -> dialog.dismiss() }
                .show()
    }

    private fun hideProgressBar() {
            appbar.visibility = View.VISIBLE
            list_item.visibility = View.VISIBLE
            progress_bar.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
            MenuInflater(this).inflate(R.menu.menu_note, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    viewModel.onBackPressed()
                    true
                }
                R.id.palette -> viewModel.togglePalette().let { true }
                R.id.delete -> viewModel.startDelDialog().let { true }
                else -> super.onOptionsItemSelected(item)
            }

    private fun changeBackgroundColor(color: Color) {
        toolbar.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    private fun initView() {
        field_body.removeTextChangedListener(textChangeListener)
        field_title.removeTextChangedListener(textChangeListener)
        note?.let {
            field_title.setText(it.title)
            field_body.setText(it.notes)
            changeBackgroundColor(it.color)
            toolbar.title = it.lastChanged
        } ?: toolbar.let {
            changeBackgroundColor(Color.WHITE)
            it.title = getString(R.string.new_note)
        }

        field_title.addTextChangedListener(textChangeListener)
        field_body.addTextChangedListener(textChangeListener)
    }

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }
}
