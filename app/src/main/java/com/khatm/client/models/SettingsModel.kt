package com.khatm.client.models

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@Entity(tableName = "settings")
data class SettingsModel(
    @PrimaryKey val version: Int,
    @ColumnInfo val books: Map<String, Int>,
    @ColumnInfo val platforms: Map<String, Int>
)

interface SettingsApi {

    @POST("settings")
    fun getSettingsAsync(@Body request: RequestBody) : Deferred<Response<SettingsModel>>
}

@Dao
interface SettingsDao {

    @get:Query("SELECT * from settings")
    val settings: LiveData<SettingsModel?>

    @Insert
    fun insert(user: SettingsModel)
}