package com.khatm.client.domain.interactors

import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository

class StateInteractor(val settingsRepository: SettingsRepository, val profileRepository: ProfileRepository) {
    val settings = settingsRepository.getSettings()

    val authState: Boolean
        get() {
            return false
        }

    fun authenticate() {

    }

    fun deauthenticate() {

    }
}