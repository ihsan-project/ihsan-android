package com.khatm.client.application.viewmodels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.khatm.client.BuildConfig
import com.khatm.client.R
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*


class AuthViewModelFactory(
    val activity: AppCompatActivity,
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>): T =
        modelClass.getConstructor(
            AppCompatActivity::class.java,
            SettingsRepository::class.java,
            ProfileRepository::class.java
        ).newInstance(activity, settingsRepository, profileRepository)
}

class AuthViewModel(val activity: AppCompatActivity,
                    val settingsRepository: SettingsRepository,
                    val profileRepository: ProfileRepository) : ViewModelBase() {

    private val stateInteractor = StateInteractor(
        activity = activity,
        scope = scope,
        profileRepository = profileRepository,
        settingsRepository = settingsRepository)
    private var mGoogleSignInClient: GoogleSignInClient

    init {
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    val signInIntent: Intent
        get() {
            return mGoogleSignInClient.signInIntent
        }

    val versionString: String
        get() {
            val versionCode = BuildConfig.VERSION_CODE
            val versionName = BuildConfig.VERSION_NAME

            return "v.${versionName} code ${versionCode}"
        }

    fun authorizeWithServerAsync(googleAuthData: Intent) : Deferred<UserModel?> {
        val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(googleAuthData).getResult(
            ApiException::class.java)

        return stateInteractor.syncAuthentication(googleAccount)
    }

    fun deauthorizeAsync() : Deferred<Boolean> {
        mGoogleSignInClient.signOut()

        return stateInteractor.unsyncAuthentication()
    }
}