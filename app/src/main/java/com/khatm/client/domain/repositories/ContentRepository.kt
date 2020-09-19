package com.khatm.client.domain.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.factories.DatabaseFactory
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.models.SettingsModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

class ContentRepository(private val application : Application,
                        private  val scope : CoroutineScope
) {
    private val settingsDao: SettingsDao?
    private val settingsApi : SettingsApi = ApiFactory.retrofit.create(SettingsApi::class.java)
    private val booksDao: BooksDao?
    private val booksApi : BooksApi = ApiFactory.retrofit.create(BooksApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(application)
        settingsDao = db?.settingsDao()
        booksDao = db?.booksDao()
    }

    suspend fun getSettingsFromServer(currentVersion: Int) : SettingsModel? {
        val response = ApiFactory.call(
            call = { settingsApi.getSettingsAsync(currentVersion).await() },
            errorMessage = "Server not responding",
            context = application.applicationContext)

        return response;
    }

    val settings: LiveData<SettingsModel?>?
        get() {
            return settingsDao?.settings
        }

    fun store(settings : SettingsModel) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            settingsDao?.insert(settings)

            future.complete(true)
        }
        return future
    }

    suspend fun getBooksFromServer() : Books? {
        val response = ApiFactory.call(
            call = { booksApi.getBooksAsync().await() },
            errorMessage = "Could not load books",
            context = application.applicationContext)

        return response;
    }

    val books: LiveData<List<BookModel>?>?
        get() {
            return booksDao?.books
        }

    fun store(books : List<BookModel>) : Deferred<Boolean> {
        val future = CompletableDeferred<Boolean>()
        scope.launch {
            booksDao?.insert(books)

            future.complete(true)
        }
        return future
    }

}


interface SettingsApi {

    @GET("settings/{version}")
    fun getSettingsAsync(@Path("version") version: Int) : Deferred<Response<SettingsModel>>
}

@Dao
interface SettingsDao {

    @get:Query("SELECT * from settings ORDER BY version DESC")
    val settings: LiveData<SettingsModel?>

    @Insert
    fun insert(user: SettingsModel)
}


interface BooksApi {

    @GET("books")
    fun getBooksAsync() : Deferred<Response<Books>>
}

@Dao
interface BooksDao {

    @get:Query("SELECT * from books")
    val books: LiveData<List<BookModel>?>

    @Insert
    fun insert(books: List<BookModel>)
}