package com.khatm.client.activities

import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityIntent: Intent

        // go straight to main if a token is stored
//        if (Util.getToken() != null) {
//            activityIntent = Intent(this, AuthActivity::class.java)
//        } else {
//            activityIntent = Intent(this, LoginActivity::class.java)
//        }

//        startActivity(activityIntent)
//        finish()
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch(Dispatchers.Main) {

        }
    }
}