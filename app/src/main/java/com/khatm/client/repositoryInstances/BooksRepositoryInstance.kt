package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
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

    override suspend fun booksFromServer(page: Int) : Books? {
        val response = ApiFactory.call(
            call = { booksApi.getBooks(page).await() },
            errorMessage = "Could not load books",
            context = activity.application.applicationContext)

        return response;
    }
}

interface BooksApi {

    @GET("books")
    fun getBooks(@retrofit2.http.Query("page") page: Int) : Deferred<Response<Books>>
}

@Dao
interface BooksDao {

    @get:Query("SELECT * from books")
    val books: LiveData<List<BookModel>?>

    @Insert
    fun insert(books: List<BookModel>)
}