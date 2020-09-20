package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.repositories.SettingsRepository
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class SettingsRepositoryInstance(private val activity: AppCompatActivity) : SettingsRepository {
    private val settingsDao: SettingsDao?
    private val settingsApi : SettingsApi = ApiFactory.retrofit.create(SettingsApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(activity.application)
        settingsDao = db?.settingsDao()
    }

    override val settingsFromDbAsync : Deferred<SettingsModel?>
        get() {
            val future = CompletableDeferred<SettingsModel?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                settingsDao?.settings?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    override fun storeToDbAsync(settings : SettingsModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()

        // Dispatch to main thread: https://stackoverflow.com/a/54090499
        GlobalScope.launch(Dispatchers.Main) {
            settingsDao?.insert(settings)

            future.complete(true)
        }

        return future
    }

    override suspend fun settingsFromServer(currentVersion: Int) : SettingsModel? {
        val response = ApiFactory.call(
            call = { settingsApi.getSettingsAsync(currentVersion).await() },
            errorMessage = "Server not responding",
            context = activity.application.applicationContext)

        return response;
    }
}

interface SettingsApi {

    @GET("settings/{version}")
    fun getSettingsAsync(@Path("version") version: Int) : Deferred<Response<SettingsModel>>
}

@Dao
interface SettingsDao {

    @get:Query("SELECT * from settings ORDER BY version DESC")
    val settings: LiveData<SettingsModel?>

    @Insert
    fun insert(user: SettingsModel)
}