package com.khatm.client.viewmodels

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.khatm.client.ApiFactory
import com.khatm.client.BuildConfig
import com.khatm.client.models.User
import com.khatm.client.repositories.UserRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FirstViewModel : ViewModel() {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private val repository : UserRepository = UserRepository(ApiFactory.khatmApi)

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var activity: AppCompatActivity

    val userLiveData = MutableLiveData<User>()

    val isLoggedIn: Boolean
        get() {
            return GoogleSignIn.getLastSignedInAccount(activity) != null
        }

    val signInIntent: Intent
        get() {
            return mGoogleSignInClient.signInIntent
        }

    fun setupGoogleClientFor(loginActivity: AppCompatActivity) {
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
    }

    /*
     * After the user signs in, can get a GoogleSignInAccount object for the user in the activity's onActivityResult method.
     * Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
     */
    fun googleAccount(data: Intent): GoogleSignInAccount? {
        return GoogleSignIn.getSignedInAccountFromIntent(data).getResult(
                ApiException::class.java)
    }

    fun authenticateAsync(activity: AppCompatActivity) : Deferred<User?> {
        val apiResult = CompletableDeferred<User?>()

        scope.launch {
            val authentication = repository.getAuthentication("monkey", "butt@butt.com", "poop")
            userLiveData.postValue(authentication)
        }

        userLiveData.observe(activity, Observer {
            apiResult.complete(it)
        })

        return apiResult
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}