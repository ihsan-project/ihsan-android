package com.khatm.client.domain.interactors

import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.SSOAccount
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*

class StateInteractor(private val settingsRepository: SettingsRepository,
                      private val profileRepository: ProfileRepository) : InteractorBase() {

    fun syncSettingsAsync() : Deferred<SettingsModel?> {
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

    val loginState: Deferred<Boolean>
        get() {
            val future = CompletableDeferred<Boolean>()

            scope.launch {
                val user = profileRepository.profileFromDbAsync.await()

                if (user?.access != null && user?.access?.isNotBlank()) {
                    future.complete(true)
                } else {
                    future.complete(false)
                }
            }

            return future
        }

    fun syncAuthenticationAsync(account: SSOAccount) : Deferred<UserModel?> {
        val future = CompletableDeferred<UserModel?>()

        scope.launch {
            val settings = settingsRepository.settingsFromDbAsync.await()

            val authenticatedProfile = profileRepository.authorizeWithServer(
                account.id,
                account.email,
                account.displayName,
                account.idToken,
                settings?.constants?.platforms?.get("google")
            )

            authenticatedProfile?.let {
                profileRepository.storeToDb(it)

                it.access?.let {
                    if (it.isNotBlank()) {
                        // TODO: This is breaking dependency rule
                        ApiFactory.authToken = it
                    }
                }
            }

            future.complete(authenticatedProfile)
        }

        return future
    }

    fun unsyncAuthenticationAsync() : Deferred<Boolean> {
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