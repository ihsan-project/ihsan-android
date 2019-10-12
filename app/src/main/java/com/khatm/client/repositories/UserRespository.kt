package com.khatm.client.repositories

import com.khatm.client.models.KhatmApi
import com.khatm.client.models.User

class UserRepository(private val api : KhatmApi) : Repository() {

    suspend fun getAuthentication() : User?{

        val response = safeApiCall(
            call = { api.getAuthentication().await() },
            errorMessage = "Error Fetching Authentication"
        )

        return response;
    }

}