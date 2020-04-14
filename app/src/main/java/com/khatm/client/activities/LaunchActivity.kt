package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.khatm.client.extensions.dismissLoading
import com.khatm.client.extensions.displayLoading
import com.khatm.client.viewmodels.AuthViewModel
import com.khatm.client.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LaunchActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        authViewModel.setupFor(this)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingsViewModel.setupFor(this)
    }

    override fun onStart() {
        super.onStart()

        displayLoading()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val settings = settingsViewModel.getSettingsAsync().await()

                settings?.let {
                    settingsViewModel.storeSettingsAsync(it).await()

                    Log.d("LaunchActivity", "Load settings success")
                }
            }
            catch (e: ApiException) {
                Log.d("LaunchActivity", "Failed Settings: $e")
                Toast.makeText(this@LaunchActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
            }

            val user = authViewModel.authorizedUserAsync.await()
            var intent = Intent(this@LaunchActivity, AuthActivity::class.java)
            
            user?.access?.let {
                if (it.isNotBlank()) {
                    Log.d("LaunchActivity", "Already Logged in")
                    intent = Intent(this@LaunchActivity, HomeActivity::class.java)
                }
            }

            dismissLoading()
            startActivity(intent)
            finish()
        }
    }

}