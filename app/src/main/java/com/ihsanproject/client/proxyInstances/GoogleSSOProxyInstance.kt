package com.ihsanproject.client.proxyInstances

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ihsanproject.client.R
import com.ihsanproject.client.application.proxies.GoogleSSOProxy
import com.ihsanproject.client.domain.repositories.SSOAccount

class GoogleSSOProxyInstance(val activity: AppCompatActivity) : GoogleSSOProxy {

    private lateinit var _signinIntent: suspend (Intent) -> Intent?
    override var signinIntent: suspend (Intent) -> Intent?
        get() = _signinIntent
        set(value) {
            _signinIntent = value
        }

    private var googleSignInClient: GoogleSignInClient

    init {
        val gso: GoogleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    override suspend fun signIn(): SSOAccount? {
        val intent = _signinIntent(googleSignInClient.signInIntent)
        intent?.let {
            val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(it).getResult(
                ApiException::class.java)

            return SSOAccount(
                id = googleAccount?.id,
                email = googleAccount?.email,
                displayName = googleAccount?.displayName,
                idToken = googleAccount?.idToken
            )
        }

        return null
    }

    override suspend fun signOut() {
        googleSignInClient.signOut()
    }
}