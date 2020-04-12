package com.khatm.client.repositories
import android.app.Application
import androidx.lifecycle.LiveData
import com.khatm.client.ApiFactory
import com.khatm.client.models.UserModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import com.khatm.client.models.UserDao
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch


class UserRepository(private val application : Application,
                     private  val scope : CoroutineScope) {
    private val userDao: UserDao?

    init {
        val db = DatabaseFactory.getDatabase(application)
        userDao = db?.userDao()
    }

    suspend fun getAuthorizationFromServer(uuid: String?, email: String?, firstName: String?, idToken: String?) : UserModel? {

        // TODO: Create a serializer in UserModel model class to help with this
        val json = JSONObject()
        json.put("email", email)
        json.put("uuid", uuid)
        json.put("first_name", firstName)
        json.put("digest", idToken)
        json.put("platform", 1)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val response = ApiFactory.call(
            call = { ApiFactory.api.getAuthorizationAsync(requestBody).await() },
            errorMessage = "Error Fetching Authorization")

        return response;
    }

    val authorizedUser: LiveData<UserModel?>?
        get() {
            return userDao?.authorizedUser
        }

    fun store(user : UserModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            userDao?.insert(user)

            future.complete(true)
        }
        return future
    }

    fun clear() : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            userDao?.deleteAll()

            future.complete(true)
        }

        return future
    }

}