package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.viewmodels.FirstViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

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
        runBlocking {
            val account = googleSigninFlow()

            if (account != null) {
                goToNextScreen()
            }
        }
    }

    suspend fun googleSigninFlow(): GoogleSignInAccount? {
        return suspendCoroutine<GoogleSignInAccount> { continuation ->
            startActivityForResult(firstViewModel.signInIntent, firstViewModel.authUUID)

            googleSigninAccountContinuation = continuation
        }
    }

    var googleSigninAccountContinuation: Continuation<GoogleSignInAccount>? = null

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("MainActivity.kt", "onActivityResult() called")

        super.onActivityResult(requestCode, resultCode, data)

        try {
            val account = firstViewModel.googleAccount(data, requestCode)

            Log.d("MainActivity.kt", "mmi: account: $account")

            // Signed in successfully
            if (account != null &&
                googleSigninAccountContinuation != null) {
                (googleSigninAccountContinuation as Continuation<GoogleSignInAccount>).resume(account)
            }
        }
        catch (e: ApiException) {
            Log.e("MainActivity.kt", "mmi: error: $e")
            // The ApiException status code indicates the detailed failure reason.
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun goToNextScreen() {
        Log.d("MainActivity.kt", "goToNextScreen() called")

        Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
