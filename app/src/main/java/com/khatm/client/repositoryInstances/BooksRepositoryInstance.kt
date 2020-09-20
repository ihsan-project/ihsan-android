package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.http.GET

class BooksRepositoryInstance(private val activity: AppCompatActivity) : BooksRepository {
    private val booksDao: BooksDao?
    private val booksApi : BooksApi = ApiFactory.retrofit.create(BooksApi::class.java)

    init {
        val db = DatabaseFactory.getDatabase(activity.application)
        booksDao = db?.booksDao()
    }

    override suspend fun booksFromServer() : Books? {
        val response = ApiFactory.call(
            call = { booksApi.getBooksAsync().await() },
            errorMessage = "Could not load books",
            context = activity.application.applicationContext)

        return response;
    }

    override val booksFromDbAsync : Deferred<List<BookModel>?>
        get() {
            val future = CompletableDeferred<List<BookModel>?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                booksDao?.books?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    override fun storeToDb(books : List<BookModel>) {
        booksDao?.insert(books)
    }
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