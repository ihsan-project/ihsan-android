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
import kotlin.coroutines.CoroutineContext

class AuthViewModelFactory(
    val activity: AppCompatActivity,
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return AuthViewModelFactory(activity, settingsRepository, profileRepository) as T
    }
}

class AuthViewModel(val activity: AppCompatActivity,
                    val settingsRepository: SettingsRepository,
                    val profileRepository: ProfileRepository) : ViewModel() {

    // TODO: These are probably going to be required in every VM
    //       Is it possilbe to create a Parent class VM that all VMs inherit from?
    //       Or extend VMs so they all include these common properties?
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val stateInteractor = StateInteractor(activity = activity, profileRepository = profileRepository, settingsRepository = settingsRepository)

    private lateinit var mGoogleSignInClient: GoogleSignInClient

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

        return stateInteractor.syncAuthentication(scope, googleAccount)
    }

    fun deauthorizeAsync() : Deferred<Boolean> {
        mGoogleSignInClient.signOut()

        return stateInteractor.unsyncAuthentication(scope)
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}