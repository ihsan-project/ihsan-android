package com.ihsanproject.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ihsanproject.client.ApiFactory
import com.ihsanproject.client.domain.models.SettingsModel
import com.ihsanproject.client.domain.repositories.SettingsRepository
import com.ihsanproject.client.factories.DatabaseFactory
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

    override fun storeToDb(settings : SettingsModel) {
        settingsDao?.insert(settings)
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