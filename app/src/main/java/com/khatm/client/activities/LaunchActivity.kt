package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.khatm.client.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LaunchActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.setupFor(this)
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch(Dispatchers.Main) {
            val user = authViewModel.authorizedUserAsync.await()
            var intent = Intent(this@LaunchActivity, AuthActivity::class.java)
            
            user?.access?.let {
                if (it.isNotBlank()) {
                    Log.d("LaunchActivity", "Already Logged in")
                    intent = Intent(this@LaunchActivity, HomeActivity::class.java)
                }
            }

            startActivity(intent)
            finish()
        }
    }
}