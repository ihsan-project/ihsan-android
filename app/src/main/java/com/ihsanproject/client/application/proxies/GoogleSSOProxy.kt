package com.ihsanproject.client.application.proxies

import android.content.Intent
import com.ihsanproject.client.domain.repositories.SSOAccount

interface GoogleSSOProxy {
    var signinIntent: suspend (Intent) -> (Intent?)
    suspend fun signIn(): SSOAccount?
    suspend fun signOut()
}