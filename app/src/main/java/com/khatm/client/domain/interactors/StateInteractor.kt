package com.khatm.client.domain.interactors

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*

class StateInteractor(private val activity: AppCompatActivity,
                      private val scope: CoroutineScope,
                      private val settingsRepository: SettingsRepository,
                      private val profileRepository: ProfileRepository) {

    fun syncSettings() : Deferred<SettingsModel?> {
        val future = CompletableDeferred<SettingsModel?>()

        scope.launch {
            var settings = settingsRepository.settingsFromDbAsync.await()
            if (settings == null) {
                // This might the first time the user is opening the app
                settings = settingsRepository.settingsFromServer(0)
            } else {
                settings = settingsRepository.settingsFromServer(settings.version)
            }

            settings?.let {
                settingsRepository.storeToDb(it)
            }

            future.complete(settings)
        }

        return future
    }

    fun syncUser() : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()

        scope.launch {
            val user = profileRepository.profileFromDbAsync.await()

            user?.access?.let {
                if (it.isNotBlank()) {
                    ApiFactory.authToken = it
                }
            }

            future.complete(user)
        }

        return future
    }

    fun syncAuthentication(googleAccount: GoogleSignInAccount?) : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()

        scope.launch {
            val settings = settingsRepository.settingsFromDbAsync.await()

            val authenticatedProfile = profileRepository.authorizeWithServer(
                googleAccount?.id,
                googleAccount?.email,
                googleAccount?.displayName,
                googleAccount?.idToken,
                settings?.constants?.platforms?.get("google")
            )

            authenticatedProfile?.let {
                profileRepository.storeToDb(it)

                if (it.access.isNotBlank()) {
                    ApiFactory.authToken = it.access
                }
            }

            future.complete(authenticatedProfile)
        }

        return future
    }

    fun unsyncAuthentication() : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()

        scope.launch {
            val user = profileRepository.profileFromDbAsync.await()

            user?.let {
                profileRepository.deleteFromDb(it)
                ApiFactory.authToken = null
            }

            future.complete(true)
        }

        return future
    }
}