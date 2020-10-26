package com.badmitry.kotlingeekbrains.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.badmitry.kotlingeekbrains.ui.BaseActivity
import com.badmitry.kotlingeekbrains.ui.main.MainActivity
import com.badmitry.kotlingeekbrains.vm.SplashViewModel

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {
    companion object {
        fun start(context: Context) = Intent(context, SplashActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override val viewModel by lazy { ViewModelProvider(this).get(SplashViewModel::class.java) }
    override val layoutRes: Int? = null

    override fun onResume() {
        super.onResume()
        viewModel.requestUser()
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            viewModel.startMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStartMainActivityLiveData().observe(this, { startMainActivity() })
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }


}