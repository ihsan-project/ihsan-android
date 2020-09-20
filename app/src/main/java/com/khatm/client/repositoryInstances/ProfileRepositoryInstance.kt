package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.UserModel
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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

    override fun storeToDb(profile : UserModel) {
        userDao?.insert(profile)
    }

    override fun deleteFromDb(profile: UserModel) {
        // TODO: Need to delete only the specified user
        userDao?.deleteAll()
    }
}

interface UserApi {
    @POST("authorizations")
    fun getAuthorizationAsync(@Body request: RequestBody) : Deferred<Response<UserModel>>
}

@Dao
interface UserDao {

    @get:Query("SELECT * from user WHERE access IS NOT NULL")
    val profile: LiveData<UserModel?>

    @Insert
    fun insert(user: UserModel)

    @Query("DELETE FROM user")
    fun deleteAll()
}