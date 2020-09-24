package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.BuildConfig
import com.khatm.client.application.proxies.GoogleSSOProxy
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository


class AuthViewModelFactory(
    val activity: AppCompatActivity,
    val settingsRepository: SettingsRepository,
    val profileRepository: ProfileRepository,
    val googleSSOProxy: GoogleSSOProxy
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>): T =
        modelClass.getConstructor(
            AppCompatActivity::class.java,
            SettingsRepository::class.java,
            ProfileRepository::class.java,
            GoogleSSOProxy::class.java
        ).newInstance(activity, settingsRepository, profileRepository, googleSSOProxy)
}

class AuthViewModel(val activity: AppCompatActivity,
                    val settingsRepository: SettingsRepository,
                    val profileRepository: ProfileRepository,
                    val googleSSOProxy: GoogleSSOProxy) : ViewModelBase() {

    private val stateInteractor = StateInteractor(settingsRepository, profileRepository)

    val versionString: String
        get() {
            val versionCode = BuildConfig.VERSION_CODE
            val versionName = BuildConfig.VERSION_NAME

            return "v.${versionName} code ${versionCode}"
        }

    suspend fun authorize() : UserModel? {
        val account = googleSSOProxy.signIn()

        account?.let {
            return stateInteractor.syncAuthenticationAsync(it).await()
        }

        return null
    }

    suspend fun deauthorize() {
        googleSSOProxy.signOut()

        stateInteractor.unsyncAuthenticationAsync().await()
    }
}