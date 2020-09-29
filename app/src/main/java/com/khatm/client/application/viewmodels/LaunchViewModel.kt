package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository

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
                      val profileRepository: ProfileRepository) : ViewModelBase() {

    private val stateInteractor = StateInteractor(settingsRepository, profileRepository)

    suspend fun syncSettings() : SettingsModel? {
        return stateInteractor.syncSettingsAsync().await()
    }

    suspend fun isLoggedIn() : Boolean {
        return stateInteractor.loginState.await()
    }
}