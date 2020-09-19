package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.SettingsModel

interface SettingsRepository {
    fun getSettings() : SettingsModel
}