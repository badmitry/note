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

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {
    companion object {
        private val RC_SIGN_IN = 458
    }

    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutRes: Int?
    private var error: Throwable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let {
            setContentView(it)
        }
        viewModel.getViewState().observe(this, { value ->
            value ?: return@observe
            error = value.error
            error?.let {
                renderError(it)
                return@observe
            }
            renderData(value.data)
        })
        viewModel.getNotAuthenticationLiveData().observe(this, { startLoginActivity() })
        viewModel.getNotInternetConnectionLiveData().observe(this, { alertAboutInternetConnection() })
        viewModel.getIsReconnectionLiveData().observe(this, { value ->
            if (value) {
                error?.let {
                    renderError(it)
                } ?: finish()
            } else {
                finish()
            }
        })
        viewModel.getShowErrorLiveData().observe(this, { value ->
            showError(value)
        })
    }

    private fun alertAboutInternetConnection() {
        AlertDialog.Builder(this)
                .setTitle(R.string.internet_connection_varning)
                .setMessage(R.string.internet_connection_message)
                .setPositiveButton(R.string.internet_reconnection) { dialog, which ->
                    viewModel.reconnectionToInternet(true)
                }
                .setNegativeButton(R.string.exist) { dialog, which -> viewModel.reconnectionToInternet(false) }
                .show()
    }

    protected fun renderError(error: Throwable) {
        viewModel.renderError(error)
    }

    protected fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    abstract fun renderData(data: T)

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
}