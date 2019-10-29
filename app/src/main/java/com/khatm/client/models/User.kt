package com.khatm.client.models

import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val access: String
)

interface KhatmApi {
    @POST("authentications")
    fun getAuthentication(@Body request: RequestBody) : Deferred<Response<User>>
}