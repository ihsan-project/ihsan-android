package com.khatm.client.application.proxies

import android.content.Intent
import com.khatm.client.domain.repositories.SSOAccount

interface GoogleSSOProxy {
    var signinIntent: suspend (Intent) -> (Intent?)
    suspend fun signIn(): SSOAccount?
    suspend fun signOut()
}