package com.badmitry.kotlingeekbrains.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.model.Color
import com.badmitry.kotlingeekbrains.data.model.Note
import com.badmitry.kotlingeekbrains.ui.App
import com.badmitry.kotlingeekbrains.vm.NoteViewModel
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_NOTE = "note"
        fun startNoteActivity(context: Context, note: Note? = null) = Intent(context, NoteActivity::class.java).apply {
            putExtra(EXTRA_NOTE, note)
            context.startActivity(this)
        }
    }

    private var note: Note? = null
    private lateinit var viewModel: NoteViewModel


    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) = viewModel.saveNote(field_title.text.toString(), field_body.text.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        note = intent.getParcelableExtra(EXTRA_NOTE)
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        viewModel.pendingNote = note

        supportActionBar?.title = note?.lastChanged ?: getString(R.string.new_note)
        viewModel.getLiveDataOnBackPressed().observe(this, { value ->
            onBackPressed()
        })
        viewModel.getLiveDataIfTitleLessThree().observe(this, {
            Toast.makeText(this, "Заголовок должен содержать не менее 3 символов!", Toast.LENGTH_SHORT).show()
        })
        initView()
    }

    private fun initView() {
        field_body.removeTextChangedListener(textChangeListener)
        field_title.removeTextChangedListener(textChangeListener)
        note?.let {
            field_title.setText(it.title)
            field_body.setText(it.notes)

            val color = when (it.color) {
                Color.WHITE -> R.color.color_white
                Color.YELLOW -> R.color.color_yellow
                Color.GREEN -> R.color.color_green
                Color.BLUE -> R.color.color_blue
                Color.RED -> R.color.color_red
                Color.VIOLET -> R.color.color_violet
                Color.PINK -> R.color.color_pink
            }

            toolbar.setBackgroundColor(ResourcesCompat.getColor(resources, color, null))
        }

        field_title.addTextChangedListener(textChangeListener)
        field_body.addTextChangedListener(textChangeListener)
    }

//    private fun saveNote() {
//        viewModel.saveNote(field_title.text, field_body.text)
//        if (field_title.text == null || field_title.text!!.length < 3) return
//
//        note = note?.copy(
//                title = field_title.text.toString(),
//                notes = field_body.text.toString(),
//                lastChanged = Date()
//        )
//                ?: Note(UUID.randomUUID().toString(), field_title.text.toString(), field_body.text.toString())
//
//        note?.let { viewModel.save(it) }
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            viewModel.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
