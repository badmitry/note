package com.badmitry.kotlingeekbrains.ui.splash

import android.content.Context
import android.content.Intent
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.ui.main.MainActivity
import com.badmitry.kotlingeekbrains.vm.SplashViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity<Boolean?>() {
    companion object {
        fun start(context: Context) = Intent(context, SplashActivity::class.java).apply {
            context.startActivity(this)
        }
    }
    private lateinit var startMainActivityJob: Job
    override val viewModel: SplashViewModel by viewModel()
    override val layoutRes: Int? = null

    override fun onResume() {
        super.onResume()
        viewModel.requestUser()
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            launch {
                viewModel.startMainActivity()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startMainActivityJob = launch {
            viewModel.getStartMainActivityChannel().consumeEach {
                startMainActivity()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        startMainActivityJob.cancel()
    }

    private fun startMainActivity() {
        finish()
        MainActivity.start(this)
    }
}