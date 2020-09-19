package com.khatm.client.application.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository

class LaunchViewModelFactory(
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return LaunchViewModelFactory(settingsRepository, profileRepository) as T
    }
}

class LaunchViewModel(val settingsRepository: SettingsRepository,
                      val profileRepository: ProfileRepository) : ViewModel() {

    val stateInteractor = StateInteractor(profileRepository = profileRepository, settingsRepository = settingsRepository)

    fun syncSettings() {

    }

    val isLoggedIn : Boolean
        get() {
            return stateInteractor.authState
        }
}