package com.ihsanproject.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.ihsanproject.client.ApiFactory
import com.ihsanproject.client.R
import com.ihsanproject.client.application.viewmodels.AuthViewModel
import com.ihsanproject.client.application.viewmodels.AuthViewModelDelegate
import com.ihsanproject.client.application.viewmodels.AuthViewModelFactory
import com.ihsanproject.client.proxyInstances.GoogleSSOProxyInstance
import com.ihsanproject.client.repositoryInstances.ProfileRepositoryInstance
import com.ihsanproject.client.repositoryInstances.SettingsRepositoryInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthActivity : ActivityBase(), AuthViewModelDelegate {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository = SettingsRepositoryInstance(this)
        val profileRepository = ProfileRepositoryInstance(this)
        val googleSSOProxy = GoogleSSOProxyInstance(this)
        googleSSOProxy.signinIntent = {
            launchIntentAsync(it).await()?.data
        }

        authViewModel = ViewModelProviders
            .of(this, AuthViewModelFactory(this, settingsRepository, profileRepository, googleSSOProxy))
            .get(AuthViewModel::class.java)
        authViewModel.delegate = this

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
            try {
                val user = authViewModel.authorize()

                if (user?.access != null) {
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

    override suspend fun setAuthToken(token: String?) {
        ApiFactory.authToken = token
    }

    override suspend fun clearAuthToken() {
        ApiFactory.authToken = null
    }
}
