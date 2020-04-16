package com.khatm.client

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

object ApiFactory {

    private val authInterceptor = Interceptor {chain->
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("x-api-key", BuildConfig.apiKey)
            .build()

        chain.proceed(newRequest)
    }

    private val httpClient = OkHttpClient().newBuilder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit : Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("${BuildConfig.apiUrl}/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    suspend fun <T : Any> call(call: suspend () -> Response<T>, errorMessage: String, context: Context): T? {

        val result : Result<T> = safeApiResult(call, errorMessage)
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                // TODO: If 401 then log user out
                Log.e("ApiFactory", "$errorMessage; ${result.exception}")
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            is Result.SuccessEmpty ->
                Log.e("ApiFactory", "No Data")
        }

        return data
    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T> {
        val response = call.invoke()
        if (response.isSuccessful) {
            if (response.code() == 204) {
                return Result.SuccessEmpty()
            }

            return Result.Success(response.body()!!)
        }

        return Result.Error(IOException(errorMessage))
    }
}

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    class SuccessEmpty() : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>()
}