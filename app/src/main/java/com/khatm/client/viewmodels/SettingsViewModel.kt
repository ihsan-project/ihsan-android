package com.khatm.client.viewmodels


import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.khatm.client.models.SettingsModel
import com.khatm.client.repositories.SettingsRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SettingsViewModel() : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    private lateinit var settingsRepository : SettingsRepository
    private lateinit var activity: AppCompatActivity


    fun setupFor(authActivity: AppCompatActivity) {
        activity = authActivity
        settingsRepository = SettingsRepository(activity.application, scope)
    }

    fun settingsAsync() : Deferred<SettingsModel?> {
        val future = CompletableDeferred<SettingsModel?>()

        scope.launch {
            val settings = settingsRepository.getSettingsFromServer()
            future.complete(settings)
        }

        return future
    }

    fun storeSettingsAsync(settings: SettingsModel) : Deferred<Boolean> {
        return settingsRepository.store(settings)
    }
}