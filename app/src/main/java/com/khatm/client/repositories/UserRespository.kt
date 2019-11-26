package com.khatm.client.repositories
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.khatm.client.models.KhatmApi
import com.khatm.client.models.User
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import com.khatm.client.models.UserDao
import com.khatm.client.factories.LocalDatabase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch


class UserRepository(private val application : Application,
                     private val api : KhatmApi,
                     private  val scope : CoroutineScope) : RepositoryBase() {
    private val userDao: UserDao?

    init {
        val db = LocalDatabase.getDatabase(application)
        userDao = db?.userDao()
    }

    suspend fun getAuthentication(uuid: String?, email: String?, firstName: String?) : User? {

        // TODO: Create a serializer in User model class to help with this
        val json = JSONObject()
        json.put("email", email)
        json.put("uuid", uuid)
        json.put("first_name", firstName)
        json.put("platform", 1)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        Log.d("UserRepository", "Get Authentication $email")

        val response = apiCall(
            call = { api.getAuthenticationAsync(requestBody).await() },
            errorMessage = "Error Fetching Authentication")

        return response;
    }

    val authenticatedUser: LiveData<User?>?
        get() {
            return userDao?.authenticatedUser
        }

    fun insert(user : User) : Deferred<Boolean> {
        val completion = CompletableDeferred<Boolean>()
        scope.launch {
            userDao?.insert(user)

            completion.complete(true)
        }
        return completion
    }

    fun clear() : Deferred<Boolean> {
        val completion = CompletableDeferred<Boolean>()
        scope.launch {
            userDao?.deleteAll()

            completion.complete(true)
        }

        return completion
    }

}