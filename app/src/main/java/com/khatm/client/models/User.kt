package com.khatm.client.models

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


@Entity
data class User(
    @PrimaryKey val id: Int,
    @ColumnInfo val first_name: String,
    @ColumnInfo val email: String,
    @ColumnInfo val access: String
)

interface KhatmApi {
    @POST("authentications")
    fun getAuthenticationAsync(@Body request: RequestBody) : Deferred<Response<User>>
}

@Dao
interface UserDao {

    @get:Query("SELECT * from user WHERE access IS NOT NULL")
    val authenticatedUser: LiveData<User>

    @Insert
    fun insert(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()
}