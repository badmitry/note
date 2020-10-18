package com.badmitry.kotlingeekbrains.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.badmitry.kotlingeekbrains.vm.BaseViewModel

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {
    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        viewModel.getViewState().observe(this, { value ->
            value ?: return@observe
            value.error?.let {
                renderError(it)
                return@observe
            }
            renderData(value.data)
        })
    }

    protected fun renderError(error: Throwable) {
        error.message?.let {
            showError(it)
        }
    }

    protected fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    abstract fun renderData(data: T)
}