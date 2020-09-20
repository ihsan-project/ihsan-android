package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LaunchViewModelFactory(
    val activity: AppCompatActivity,
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>): T =
        modelClass.getConstructor(
            AppCompatActivity::class.java,
            SettingsRepository::class.java,
            ProfileRepository::class.java
        ).newInstance(activity, settingsRepository, profileRepository)
}

class LaunchViewModel(val activity: AppCompatActivity,
                      val settingsRepository: SettingsRepository,
                      val profileRepository: ProfileRepository) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val stateInteractor = StateInteractor(activity = activity, profileRepository = profileRepository, settingsRepository = settingsRepository)

    fun syncSettingsAsync() : Deferred<SettingsModel?> {
        return stateInteractor.syncSettings(scope)
    }

    fun syncProfile() : Deferred<UserModel?> {
        return stateInteractor.syncUser(scope)
    }
}