package com.khatm.client.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.khatm.client.R

class HomeActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var signOutButton: Button
    lateinit var textViewName : TextView
    lateinit var textViewEmail : TextView
    lateinit var textViewId : TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        textViewName = findViewById(R.id.TextViewName)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewId = findViewById(R.id.textViewId)
        signOutButton = findViewById(R.id.button_sign_out_google)

        /*
         * Configure sign-in to request the user's ID, email address, and basic profile.
         * ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso : GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

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


    private fun signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener {

            Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}