package com.ihsanproject.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ihsanproject.client.domain.interactors.StateInteractor
import com.ihsanproject.client.domain.models.SettingsModel
import com.ihsanproject.client.domain.repositories.ProfileRepository
import com.ihsanproject.client.domain.repositories.SettingsRepository

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

interface LaunchViewModelDelegate {
    suspend fun setAuthToken(token: String?)
}

class LaunchViewModel(val activity: AppCompatActivity,
                      val settingsRepository: SettingsRepository,
                      val profileRepository: ProfileRepository) : ViewModelBase() {

    var delegate: LaunchViewModelDelegate? = null
    private val stateInteractor = StateInteractor(settingsRepository, profileRepository)

    suspend fun syncSettings() : SettingsModel? {
        return stateInteractor.syncSettingsAsync().await()
    }

    suspend fun isLoggedIn() : Boolean {
        return stateInteractor.loginState.await()
    }

    suspend fun syncLoggedInAuth() {
        stateInteractor.loggedInUser.await()?.let {
            delegate?.setAuthToken(it.access)
        }
    }
}