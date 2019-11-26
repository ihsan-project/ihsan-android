package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.extensions.AsyncActivity
import com.khatm.client.viewmodels.AuthenticateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AsyncActivity() {

    private lateinit var authViewModel: AuthenticateViewModel

    lateinit var googleSignInButton: SignInButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this).get(AuthenticateViewModel::class.java)
        authViewModel.setupFor(this)

        setContentView(R.layout.activity_main)
        googleSignInButton = findViewById(R.id.button_sign_in_google)
        googleSignInButton.setOnClickListener {
            signInGoogleAction()
        }
    }

    override fun onStart() {
        super.onStart()

        authViewModel.repository.authenticatedUser?.observe(this, Observer {
            it?.access?.let {
                if (it.isNotBlank()) {
                    goToNextScreen()
                }
            }
        })
    }

    private fun signInGoogleAction() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = launchIntentAsync(authViewModel.signInIntent).await()

            result?.data?.let {
                try {
                    val user = authViewModel.authenticateAsync(it).await()

                    Log.d("MainActivity.kt", "user: $user")

                    if (user?.access != null) {
                        authViewModel.save(user).await()

                        goToNextScreen()
                    }
                }
                catch (e: ApiException) {
                    Log.d("MainActivity.kt", "Failed: $e")
                    Toast.makeText(this@MainActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun goToNextScreen() {
        Log.d("MainActivity.kt", "goToNextScreen() called")

        Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
