package com.khatm.client.viewmodels


import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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

    fun getSettingsAsync() : Deferred<SettingsModel?> {
        val future = CompletableDeferred<SettingsModel?>()

        scope.launch {
            val currentSettings = currentSettingsAsync.await()

            var settings: SettingsModel?
            if (currentSettings == null) {
                // This might the first time the user is opening the app
                settings = settingsRepository.getSettingsFromServer(0)
            } else {
                settings = settingsRepository.getSettingsFromServer(currentSettings.version)
            }

            future.complete(settings)
        }

        return future
    }

    val currentSettingsAsync : Deferred<SettingsModel?>
        get() {
            val future = CompletableDeferred<SettingsModel?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                settingsRepository.settings?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    fun storeSettingsAsync(settings: SettingsModel) : Deferred<Boolean> {
        return settingsRepository.store(settings)
    }
}