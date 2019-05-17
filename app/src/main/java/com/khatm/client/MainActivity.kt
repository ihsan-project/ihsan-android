package com.khatm.client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // continue with google click handler
        btnContinueWithGoogle.setOnClickListener {

            Log.i("Main Activity", "Google button was clicked")

            // TODO: Set up Google login authentication

            Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()

            // explicit intent to go to home screen
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
