package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.khatm.client.R
import com.khatm.client.extensions.AsyncActivity
import com.khatm.client.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthActivity : AsyncActivity() {

    private lateinit var authViewModel: AuthViewModel

    lateinit var googleSignInButton: SignInButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.setupFor(this)

        setContentView(R.layout.activity_auth)
        googleSignInButton = findViewById(R.id.button_sign_in_google)
        googleSignInButton.setOnClickListener {
            signInGoogleAction()
        }

        val layout: ConstraintLayout = findViewById(R.id.activity_auth);
        layout.addView(createProgressView())
    }

    private fun createProgressView() : FrameLayout {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.view_progress, null, false) as FrameLayout
        layout.setVisibility(View.VISIBLE);

        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        layout.layoutParams = params

        return layout
    }

    private fun signInGoogleAction() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = launchIntentAsync(authViewModel.signInIntent).await()

            result?.data?.let {
                try {
                    val user = authViewModel.authorizeWithServerAsync(it).await()

                    if (user?.access != null) {
                        authViewModel.saveAuthorizedUserAsync(user).await()

                        Log.d("AuthActivity", "Login successful")

                        val intent = Intent(this@AuthActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
                catch (e: ApiException) {
                    Log.d("AuthActivity", "Failed: $e")
                    Toast.makeText(this@AuthActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
