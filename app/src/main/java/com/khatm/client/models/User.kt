package com.khatm.client.models

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String
)

interface KhatmApi {
    @GET("authentications")
    fun getAuthentication() : Deferred<Response<User>>
}