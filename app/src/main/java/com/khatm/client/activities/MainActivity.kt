package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.extensions.AsyncActivity
import com.khatm.client.viewmodels.FirstViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AsyncActivity() {

    private lateinit var firstViewModel: FirstViewModel

    lateinit var googleSignInButton: SignInButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firstViewModel = ViewModelProviders.of(this).get(FirstViewModel::class.java)
        firstViewModel.setupGoogleClientFor(this)

        setContentView(R.layout.activity_main)
        googleSignInButton = findViewById(R.id.button_sign_in_google)
        googleSignInButton.setOnClickListener { signInGoogleAction() }
    }

    override fun onStart() {
        super.onStart()

        if (firstViewModel.isLoggedIn) {
            Log.d("MainActivity.kt", "Is logged in")
            goToNextScreen()
        }
    }

    private fun signInGoogleAction() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = launchIntentAsync(firstViewModel.signInIntent).await()

            result?.data?.let {
                try {
                    val account = firstViewModel.googleAccount(it)

                    val token = firstViewModel.authenticateAsync(this@MainActivity).await()

                    Log.d("MainActivity.kt", "Is logged in " + token)

                    if (account != null) {
                        goToNextScreen()
                    } else {}
                }
                catch (e: ApiException) {
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
