package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LaunchViewModelFactory(
    val activity: AppCompatActivity,
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return LaunchViewModelFactory(activity, settingsRepository, profileRepository) as T
    }
}

class LaunchViewModel(val activity: AppCompatActivity,
                      val settingsRepository: SettingsRepository,
                      val profileRepository: ProfileRepository) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val stateInteractor = StateInteractor(activity = activity, profileRepository = profileRepository, settingsRepository = settingsRepository)

    fun syncSettings() : Deferred<SettingsModel?> {
        return stateInteractor.syncSettings(scope)
    }

    val isLoggedIn : Boolean
        get() {
            return stateInteractor.authState
        }
}