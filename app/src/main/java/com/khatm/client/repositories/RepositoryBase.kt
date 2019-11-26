package com.khatm.client.repositories

import android.util.Log
import retrofit2.Response
import java.io.IOException

open class RepositoryBase {

    suspend fun <T : Any> apiCall(call: suspend () -> Response<T>, errorMessage: String): T? {

        val result : Result<T> = safeApiResult(call, errorMessage)
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                // TODO: If 404 then log user out
                Log.e("RepositoryBase", "$errorMessage; ${result.exception}")
            }
        }

        return data
    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T> {
        val response = call.invoke()
        if (response.isSuccessful) {
            return Result.Success(response.body()!!)
        }

        return Result.Error(IOException("API Error - $errorMessage"))
    }
}

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}