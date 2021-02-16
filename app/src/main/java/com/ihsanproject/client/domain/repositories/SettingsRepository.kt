package com.ihsanproject.client.domain.repositories

import com.ihsanproject.client.domain.models.SettingsModel
import kotlinx.coroutines.Deferred

interface SettingsRepository {
    val settingsFromDbAsync : Deferred<SettingsModel?>
    fun storeToDb(settings : SettingsModel)
    suspend fun settingsFromServer(currentVersion: Int) : SettingsModel?
}