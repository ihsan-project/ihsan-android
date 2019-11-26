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
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import com.khatm.client.models.UserModel

class AuthViewModel() : ViewModel() {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var repository : com.khatm.client.repositories.UserRepository
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var activity: AppCompatActivity

    val signInIntent: Intent
        get() {
            return mGoogleSignInClient.signInIntent
        }

    fun setupFor(authActivity: AppCompatActivity) {
        /*
         * Configure sign-in to request the user's ID, email address, and basic profile.
         * ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.googleWebApplicationClientId)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(authActivity, gso)
        activity = authActivity
        repository = com.khatm.client.repositories.UserRepository(activity.application, scope)
    }


    fun authorizeWithServerAsync(googleAuthData: Intent) : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()
        val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(googleAuthData).getResult(
            ApiException::class.java)

        scope.launch {
            googleAccount?.let {
                Log.d("AuthViewModel", "Authenticate API ${it.email}")

                val authentication = repository.getAuthentication(it.id, it.email, it.displayName)
                future.complete(authentication)
            }
        }

        return future
    }

    fun authorizedUserAsync() : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()

        repository.authenticatedUser?.observe(activity, Observer {
            future.complete(it)
        })

        return future
    }

    fun saveAuthorizedUserAsync(user: UserModel) : Deferred<Boolean> {
        return repository.insert(user)
    }

    fun logoutAsync() : Deferred<Boolean> {
        mGoogleSignInClient.signOut()

        return repository.clear()
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}