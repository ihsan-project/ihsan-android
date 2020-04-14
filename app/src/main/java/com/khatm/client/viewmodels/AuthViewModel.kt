package com.khatm.client.viewmodels

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.khatm.client.BuildConfig
import com.khatm.client.R
import com.khatm.client.models.SettingsModel
import com.khatm.client.models.UserModel
import com.khatm.client.repositories.SettingsRepository
import com.khatm.client.repositories.UserRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class AuthViewModel() : ViewModel() {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var userRepository : UserRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var activity: AppCompatActivity

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

    fun setupFor(authActivity: AppCompatActivity) {
        /*
         * Configure sign-in to request the user's ID, email address, and basic profile.
         * ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(authActivity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(authActivity, gso)
        activity = authActivity
        userRepository = UserRepository(activity.application, scope)
        settingsRepository = SettingsRepository(activity.application, scope)
    }

    fun authorizeWithServerAsync(googleAuthData: Intent) : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()
        val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(googleAuthData).getResult(
            ApiException::class.java)

        scope.launch {
            val settings = currentSettingsAsync.await()

            Log.d("AuthViewModel", "Google SSO Success")
            val auth = userRepository.getAuthorizationFromServer(
                googleAccount?.id,
                googleAccount?.email,
                googleAccount?.displayName,
                googleAccount?.idToken,
                settings?.constants?.platforms?.get("google")
            )
            future.complete(auth)
        }

        return future
    }

    val authorizedUserAsync : Deferred<UserModel?>
        get() {
            val future = CompletableDeferred<UserModel?>()

            userRepository.authorizedUser?.observe(activity, Observer {
                future.complete(it)
            })

            return future
        }

    val currentSettingsAsync : Deferred<SettingsModel?>
        get() {
            val future = CompletableDeferred<SettingsModel?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                settingsRepository.settings?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    fun storeAuthorizedUserAsync(user: UserModel) : Deferred<Boolean> {
        return userRepository.store(user)
    }

    fun logoutAsync() : Deferred<Boolean> {
        mGoogleSignInClient.signOut()

        return userRepository.clear()
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}