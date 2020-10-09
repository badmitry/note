package com.badmitry.kotlingeekbrains

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_toast.setOnClickListener {
            Toast.makeText(this, "toast", Toast.LENGTH_SHORT).show()
        }
    }
}