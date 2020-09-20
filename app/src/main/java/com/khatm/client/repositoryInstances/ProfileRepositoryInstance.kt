package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.SettingsModel
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.UserApi
import com.khatm.client.domain.repositories.UserDao
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

class ProfileRepositoryInstance(private val activity: AppCompatActivity) : ProfileRepository {
    private val userDao: UserDao?
    private val api : UserApi = ApiFactory.retrofit.create(UserApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(activity.application)
        userDao = db?.userDao()
    }

    override suspend fun authorizeWithServer(uuid: String?, email: String?, firstName: String?, idToken: String?, platform: Int?) : UserModel? {
        // TODO: Create a serializer in UserModel model class to help with this
        val json = JSONObject()
        json.put("email", email)
        json.put("uuid", uuid)
        json.put("first_name", firstName)
        json.put("digest", idToken)
        json.put("platform", platform)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())

        val response = ApiFactory.call(
            call = { api.getAuthorizationAsync(requestBody).await() },
            errorMessage = "Error with authorization",
            context = activity.application.applicationContext)

        return response;
    }

    override val profileFromDbAsync : Deferred<UserModel?>
        get() {
            val future = CompletableDeferred<UserModel?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                userDao?.profile?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    override fun storeToDbAsync(profile : UserModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()

        // Dispatch to main thread: https://stackoverflow.com/a/54090499
        GlobalScope.launch(Dispatchers.Main) {
            userDao?.insert(profile)

            future.complete(true)
        }

        return future
    }

    override fun deleteFromDbAsync(profile: UserModel): Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()

        // Dispatch to main thread: https://stackoverflow.com/a/54090499
        GlobalScope.launch(Dispatchers.Main) {
            // TODO: Need to delete only the specified user
            userDao?.deleteAll()

            future.complete(true)
        }

        return future
    }
}