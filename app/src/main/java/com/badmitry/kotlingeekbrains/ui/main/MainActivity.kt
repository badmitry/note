package com.badmitry.kotlingeekbrains.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.data.entity.Note
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.ui.note.NoteActivity
import com.badmitry.kotlingeekbrains.ui.splash.SplashActivity
import com.badmitry.kotlingeekbrains.vm.MainMenuViewModel
import com.badmitry.kotlingeekbrains.vm.MainViewModel
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity<List<Note>?>() {

    companion object {
        fun start(context: Context) = Intent(context, MainActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override val viewModel: MainViewModel by viewModel()

    private val viewModelForMainMenuItem: MainMenuViewModel by lazy {
        ViewModelProvider(this).get(MainMenuViewModel::class.java)
    }
    override val layoutRes = R.layout.activity_main
    private lateinit var adapter: MainAdapter
    private lateinit var onAddButtonJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        rv_notes.layoutManager = GridLayoutManager(this, 2)
        adapter = MainAdapter() {
            viewModel.startNoteActivity(it)
        }
        rv_notes.adapter = adapter
        button_add.setOnClickListener {
            viewModel.startNoteActivity(null)
        }
        viewModelForMainMenuItem.getStartLogoutDialogLiveData().observe(this, { showLogoutDialog() })
        viewModelForMainMenuItem.getLogoutOkLiveData().observe(this, { logout() })
    }

    override fun onStart() {
        super.onStart()
        onAddButtonJob = launch {
            viewModel.getStartNoteActivityChannel().consumeEach {
                it?.let{
                    NoteActivity.startNoteActivity(this@MainActivity, it.id)
                } ?: NoteActivity.startNoteActivity(this@MainActivity)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
            MenuInflater(this).inflate(R.menu.main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.logout -> viewModelForMainMenuItem.showLogoutDialog().let { true }
                else -> false
            }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.logout_menu_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout_ok) { dialog, which ->
                    viewModelForMainMenuItem.logoutOk()
                }
                .setNegativeButton(R.string.logout_cancel) { dialog, which -> dialog.dismiss() }
                .show()
    }

    fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    SplashActivity.start(this)
                    finish()
                }
    }

    override fun renderData(data: List<Note>?) {
        data?.let { adapter.notes = it }
    }
}