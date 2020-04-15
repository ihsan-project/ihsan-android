package com.khatm.client.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.factories.DatabaseFactory
import com.khatm.client.models.SettingsModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class SettingsRepository(private val application : Application,
                         private  val scope : CoroutineScope
) {
    private val dao: SettingsDao?
    private val api : SettingsApi = ApiFactory.retrofit.create(SettingsApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(application)
        dao = db?.settingsDao()
    }

    suspend fun getSettingsFromServer(currentVersion: Int) : SettingsModel? {
        val response = ApiFactory.call(
            call = { api.getSettingsAsync(currentVersion).await() },
            errorMessage = "Error Fetching Settings")

        return response;
    }

    val settings: LiveData<SettingsModel?>?
        get() {
            return dao?.settings
        }

    fun store(settings : SettingsModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            dao?.insert(settings)

            future.complete(true)
        }
        return future
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