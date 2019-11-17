package com.khatm.client.repositories

import com.khatm.client.models.KhatmApi
import com.khatm.client.models.User
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

class UserRepository(private val api : KhatmApi) : Repository() {

    suspend fun getAuthentication(uuid: String?, email: String?, firstName: String?) : User? {

        // TODO: Create a serializer in User model class to help with this
        val json = JSONObject()
        json.put("email", email)
        json.put("uuid", uuid)
        json.put("first_name", firstName)
        json.put("platform", 1)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val response = safeApiCall(
            call = { api.getAuthenticationAsync(requestBody).await() },
            errorMessage = "Error Fetching Authentication"
        )

        return response;
    }

}