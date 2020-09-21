package com.khatm.client.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.khatm.client.application.viewmodels.LaunchViewModel
import com.khatm.client.application.viewmodels.LaunchViewModelFactory
import com.khatm.client.repositoryInstances.ProfileRepositoryInstance
import com.khatm.client.repositoryInstances.SettingsRepositoryInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LaunchActivity : AppCompatActivity() {
    private lateinit var launchViewModel: LaunchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository = SettingsRepositoryInstance(this)
        val profileRepository = ProfileRepositoryInstance(this)
        launchViewModel = ViewModelProviders
            .of(this, LaunchViewModelFactory(this, settingsRepository, profileRepository))
            .get(LaunchViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        displayLoading()

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val settings = launchViewModel.syncSettingsAsync().await()

                settings?.let {
                    Log.d("LaunchActivity", "Load settings success")
                }
            }
            catch (e: ApiException) {
                Log.d("LaunchActivity", "Failed Settings: $e")
                Toast.makeText(this@LaunchActivity, "Failed: $e", Toast.LENGTH_SHORT).show()
            }

            var intent = Intent(this@LaunchActivity, AuthActivity::class.java)

            val user = launchViewModel.syncProfile().await()
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