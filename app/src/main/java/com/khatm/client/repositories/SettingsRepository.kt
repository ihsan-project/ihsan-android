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
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

class SettingsRepository(private val application : Application,
                         private  val scope : CoroutineScope
) {
    private val dao: SettingsDao?
    private val api : SettingsApi = ApiFactory.retrofit.create(SettingsApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(application)
        dao = db?.settingsDao()
    }

    suspend fun getSettingsFromServer() : SettingsModel? {
        val response = ApiFactory.call(
            call = { api.getSettingsAsync().await() },
            errorMessage = "Error Fetching Settings")

        return response;
    }

    val settings: LiveData<SettingsModel?>?
        get() {
            return dao?.settings
        }

    fun store(user : SettingsModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            dao?.insert(user)

            future.complete(true)
        }
        return future
    }

}

interface SettingsApi {

    @GET("settings")
    fun getSettingsAsync() : Deferred<Response<SettingsModel>>
}

@Dao
interface SettingsDao {

    @get:Query("SELECT * from settings")
    val settings: LiveData<SettingsModel?>

    @Insert
    fun insert(user: SettingsModel)
}