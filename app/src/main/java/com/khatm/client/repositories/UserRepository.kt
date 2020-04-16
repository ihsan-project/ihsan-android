package com.khatm.client.repositories
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.models.UserModel
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


class UserRepository(private val application : Application,
                     private  val scope : CoroutineScope) {
    private val dao: UserDao?
    private val api : UserApi = ApiFactory.retrofit.create(UserApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(application)
        dao = db?.userDao()
    }

    suspend fun getAuthorizationFromServer(uuid: String?, email: String?, firstName: String?, idToken: String?, platform: Int?) : UserModel? {

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
            context = application.applicationContext)

        return response;
    }

    val authorizedUser: LiveData<UserModel?>?
        get() {
            return dao?.authorizedUser
        }

    fun store(user : UserModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            dao?.insert(user)

            future.complete(true)
        }
        return future
    }

    fun clear() : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            dao?.deleteAll()

            future.complete(true)
        }

        return future
    }

}

interface UserApi {
    @POST("authorizations")
    fun getAuthorizationAsync(@Body request: RequestBody) : Deferred<Response<UserModel>>
}

@Dao
interface UserDao {

    @get:Query("SELECT * from user WHERE access IS NOT NULL")
    val authorizedUser: LiveData<UserModel?>

    @Insert
    fun insert(user: UserModel)

    @Query("DELETE FROM user")
    fun deleteAll()
}