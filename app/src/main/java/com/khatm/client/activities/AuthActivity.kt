package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.extensions.BaseActivity
import com.khatm.client.extensions.dismissLoading
import com.khatm.client.extensions.displayLoading
import com.khatm.client.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthActivity : BaseActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.setupFor(this)

        setContentView(R.layout.activity_auth)

        val googleSignInButton: Button = findViewById(R.id.button_sign_in_google)
        googleSignInButton.setOnClickListener {
            signInGoogleAction()
        }

        val versionTextView : TextView = findViewById(R.id.textView_version)
        versionTextView.text = authViewModel.versionString
    }

    private fun signInGoogleAction() {
        displayLoading()

        GlobalScope.launch(Dispatchers.Main) {
            val result = launchIntentAsync(authViewModel.signInIntent).await()

            result?.data?.let {
                try {
                    val user = authViewModel.authorizeWithServerAsync(it).await()

                    if (user?.access != null) {
                        authViewModel.storeAuthorizedUserAsync(user).await()

                        Log.d("AuthActivity", "Login successful")

                        val intent = Intent(this@AuthActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("AuthActivity", "Failed: server responded without access token")
                        Toast.makeText(this@AuthActivity, "Failed to authorize. Please try again.", Toast.LENGTH_SHORT).show()
                    }

                    dismissLoading()
                }
                catch (e: ApiException) {
                    Log.d("AuthActivity", "Failed Auth: $e")
                    Toast.makeText(this@AuthActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
                    dismissLoading()
                }
            }
        }
    }
}
