package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.extensions.BaseActivity
import com.khatm.client.extensions.dismissLoading
import com.khatm.client.extensions.displayLoading
import com.khatm.client.application.viewmodels.AuthViewModel
import com.khatm.client.application.viewmodels.ChallengeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity() {
    private lateinit var challengeViewModel: ChallengeViewModel

    lateinit var signOutButton: Button
    lateinit var textViewName : TextView
    lateinit var textViewEmail : TextView
    lateinit var textViewId : TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        challengeViewModel = ViewModelProviders.of(this).get(ChallengeViewModel::class.java)
        challengeViewModel.setupFor(this)

        textViewName = findViewById(R.id.TextViewName)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewId = findViewById(R.id.textViewId)
        signOutButton = findViewById(R.id.button_sign_out_google)

        signOutButton.setOnClickListener {
            signOut()
        }

        val account : GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)

        // if there is a signed in account
        if (account != null) {
            val name = account.displayName
            val email = account.email
            val id = account.id

            textViewName.setText("Name: " + name)
            textViewEmail.setText("Email: " + email)
            textViewId.setText("ID: " + id)
        }
    }


    override fun onStart() {
        super.onStart()

        displayLoading()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val settings = challengeViewModel.getBooksFromServerAsync().await()

                settings?.let {
                    challengeViewModel.storeBooksAsync(it.books).await()

                    Log.d("HomeActivity", "Load settings success")
                }
            }
            catch (e: ApiException) {
                Log.d("HomeActivity", "Failed Settings: $e")
                Toast.makeText(this@HomeActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
            }


            dismissLoading()
        }
    }


    private fun signOut() {
        val authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.setupFor(this)

        GlobalScope.launch(Dispatchers.Main) {
            authViewModel.logoutAsync().await()

            Toast.makeText(this@HomeActivity, "Successfully signed out", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@HomeActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}