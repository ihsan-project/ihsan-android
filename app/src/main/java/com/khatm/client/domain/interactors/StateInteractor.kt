package com.khatm.client.domain.interactors

import androidx.appcompat.app.AppCompatActivity
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*

class StateInteractor(val activity: AppCompatActivity, val settingsRepository: SettingsRepository, val profileRepository: ProfileRepository) {

    fun syncSettings(scope: CoroutineScope) : Deferred<SettingsModel?> {
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
                settingsRepository.storeToDbAsync(it).await()
            }

            future.complete(settings)
        }

        return future
    }

    val authState: Boolean
        get() {
            return false
        }

    fun authenticate() {

    }

    fun deauthenticate() {

    }
}