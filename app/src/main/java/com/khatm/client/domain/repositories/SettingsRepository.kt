package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.SettingsModel
import kotlinx.coroutines.Deferred

interface SettingsRepository {
    val settingsFromDbAsync : Deferred<SettingsModel?>
    fun storeToDb(settings : SettingsModel)
    suspend fun settingsFromServer(currentVersion: Int) : SettingsModel?
}