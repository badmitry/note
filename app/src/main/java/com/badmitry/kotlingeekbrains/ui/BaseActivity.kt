package com.badmitry.kotlingeekbrains.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.badmitry.kotlingeekbrains.R
import com.badmitry.kotlingeekbrains.vm.BaseViewModel
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<S> : AppCompatActivity(), CoroutineScope {
    companion object {
        private val RC_SIGN_IN = 458
    }

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Main + Job() }
    private lateinit var dataJob: Job
    private lateinit var errorJob: Job
    private lateinit var notAuthJob: Job
    private lateinit var showErrorJob: Job
    private lateinit var notInternetConnectionJob: Job
    private lateinit var isReconnectionJob: Job


    abstract val viewModel: BaseViewModel<S>
    abstract val layoutRes: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let {
            setContentView(it)
        }
    }

    override fun onStart() {
        super.onStart()
        dataJob = launch {
            viewModel.getViewState().consumeEach {
                renderData(it)
            }
        }

        errorJob = launch {
            viewModel.getErrorChannel().consumeEach {
                renderError(it)
            }
        }

        notAuthJob = launch {
            viewModel.getNotAuthenticationChannel().consumeEach {
                startLoginActivity()
            }
        }

        notInternetConnectionJob = launch {
            viewModel.getNotInternetConnectionChannel().consumeEach {
                alertAboutInternetConnection()
            }
        }

        showErrorJob = launch {
            viewModel.getShowErrorChannel().consumeEach {
                showError(it)
            }
        }
    }

    private fun alertAboutInternetConnection() {
        AlertDialog.Builder(this)
                .setTitle(R.string.internet_connection_varning)
                .setMessage(R.string.internet_connection_message)
                .setPositiveButton(R.string.internet_reconnection) { dialog, which ->
                    launch {
                        viewModel.startAuthentication()
                    }
                }
                .setNegativeButton(R.string.exist) { dialog, which ->
                    launch {
                        finish()
                    }
                }
                .show()
    }

    protected fun renderError(error: Throwable) {
        launch {
            viewModel.renderError(error)
        }
    }

    protected fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    abstract fun renderData(data: S)

    protected fun startLoginActivity() {
        val providers = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setLogo(R.drawable.s1200)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .build()
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        dataJob.cancel()
        errorJob.cancel()
        notInternetConnectionJob.cancel()
        notAuthJob.cancel()
        showErrorJob.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}