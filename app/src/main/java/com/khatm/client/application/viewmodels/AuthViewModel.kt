package com.khatm.client.application.viewmodels

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.khatm.client.ApiFactory
import com.khatm.client.BuildConfig
import com.khatm.client.R
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ContentRepository
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import com.khatm.client.domain.repositories.UserRepository
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

    private lateinit var userRepository : UserRepository
    private lateinit var contentRepository: ContentRepository
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    init {
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        userRepository = UserRepository(activity.application, scope)
        contentRepository = ContentRepository(activity.application, scope)
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
        val future = CompletableDeferred<UserModel?>()
        val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(googleAuthData).getResult(
            ApiException::class.java)

        return stateInteractor.syncAuthentication(scope, googleAccount)
    }

    fun deauthorizeAsync() : Deferred<Boolean> {
        mGoogleSignInClient.signOut()

        return stateInteractor.unsyncAuthentication(scope)
    }

    val authorizedUserAsync : Deferred<UserModel?>
        get() {
            val future = CompletableDeferred<UserModel?>()

            GlobalScope.launch(Dispatchers.Main) {
                userRepository.authorizedUser?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    fun storeAuthorizedUserAsync(user: UserModel) : Deferred<Boolean> {
        return userRepository.store(user)
    }

    fun getSettingsFromServerAsync() : Deferred<SettingsModel?> {
        val future = CompletableDeferred<SettingsModel?>()

        scope.launch {
            val currentSettings = currentSettingsAsync.await()

            var settings: SettingsModel?
            if (currentSettings == null) {
                // This might the first time the user is opening the app
                settings = contentRepository.getSettingsFromServer(0)
            } else {
                settings = contentRepository.getSettingsFromServer(currentSettings.version)
            }

            future.complete(settings)
        }

        return future
    }

    val currentSettingsAsync : Deferred<SettingsModel?>
        get() {
            val future = CompletableDeferred<SettingsModel?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                contentRepository.settings?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    fun storeSettingsAsync(settings: SettingsModel) : Deferred<Boolean> {
        return contentRepository.store(settings)
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}