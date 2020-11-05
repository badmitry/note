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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class NoteActivity : BaseActivity<Note?>() {

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
    private lateinit var hideProgressBarJob: Job
    private lateinit var onBackPressedJob: Job
    private lateinit var isTitleLessThreeJob: Job
    private lateinit var delDialogJob: Job
    private lateinit var showPaletteJob: Job
    private lateinit var changeColorJob: Job


    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) = viewModel.saveNote(field_title.text.toString(), field_body.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        noteId = intent.getStringExtra(EXTRA_NOTE)
        noteId?.let {
            launch {
                viewModel.loadNote(it)
            }
        } ?: let {
            launch {
                viewModel.hideProgressBar()
            }
        }
        colorPicker.onColorClickListener = {
            launch {
                viewModel.changeNoteColor(it)
            }
        }
        initView()
    }

    override fun onStart() {
        super.onStart()
        showPaletteJob = launch {
            viewModel.getShowPaletteChannel().consumeEach {
                if (it) {
                    colorPicker.open()
                } else {
                    colorPicker.close()
                }
            }
        }
        hideProgressBarJob = launch {
            viewModel.getHideProgressBarChannel().consumeEach {
                hideProgressBar()
            }
        }
        onBackPressedJob = launch {
            viewModel.getOnBackPressedChannel().consumeEach {
                this@NoteActivity.onBackPressed()
            }
        }
        isTitleLessThreeJob = launch {
            viewModel.isTitleLessThreeChannel().consumeEach {
                this@NoteActivity.onBackPressed()
                Toast.makeText(this@NoteActivity, "Заголовок должен содержать не менее 3 символов!", Toast.LENGTH_SHORT).show()
            }
        }
        delDialogJob = launch {
            viewModel.getStartDelDialogChannel().consumeEach {
                this@NoteActivity.startDelDialog()
            }
        }
        changeColorJob = launch {
            viewModel.getChangeColorChannel().consumeEach {
                changeBackgroundColor(it)
            }
        }
    }

    private fun startDelDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.delete_menu_title)
                .setMessage(R.string.delete_message)
                .setPositiveButton(R.string.del_ok) { dialog, which ->
                    launch {
                        viewModel.deleteNote()
                    }
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
                    launch {
                        viewModel.onBackPressed()
                    }
                    true
                }
                R.id.palette -> {
                    launch {
                        viewModel.togglePalette()
                    }
                    true
                }
                R.id.delete -> {
                    launch {
                        viewModel.startDelDialog()
                    }
                    true
                }
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
