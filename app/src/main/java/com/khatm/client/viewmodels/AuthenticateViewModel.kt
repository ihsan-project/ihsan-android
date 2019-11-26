package com.khatm.client.viewmodels

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.khatm.client.ApiFactory
import com.khatm.client.BuildConfig
import com.khatm.client.models.User
import com.khatm.client.repositories.UserRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AuthenticateViewModel() : ViewModel() {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    lateinit var repository : UserRepository
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var activity: AppCompatActivity

    val userLiveData = MutableLiveData<User>()

    val signInIntent: Intent
        get() {
            return mGoogleSignInClient.signInIntent
        }

    fun setupFor(loginActivity: AppCompatActivity) {
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
        mGoogleSignInClient = GoogleSignIn.getClient(loginActivity, gso)
        activity = loginActivity
        repository = UserRepository(activity.application, scope)
    }


    fun authenticateAsync(googleAuthData: Intent) : Deferred<User?> {
        val apiResult = CompletableDeferred<User?>()
        val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(googleAuthData).getResult(
            ApiException::class.java)

        scope.launch {
            googleAccount?.let {
                Log.d("AuthenticateViewModel", "Authenticate API ${it.email}")

                val authentication = repository.getAuthentication(it.id, it.email, it.displayName)
                userLiveData.postValue(authentication)
            }
        }

        userLiveData.observe(activity, Observer {
            apiResult.complete(it)
        })

        return apiResult
    }

    fun save(user: User) : Deferred<Boolean> {
        return repository.insert(user)
    }

    fun logout() : Deferred<Boolean> {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut()
        }

        return repository.clear()
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}