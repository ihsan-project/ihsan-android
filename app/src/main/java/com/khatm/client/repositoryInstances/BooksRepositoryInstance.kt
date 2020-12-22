package com.khatm.client.repositoryInstances

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.khatm.client.ApiFactory
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository
import com.khatm.client.factories.DatabaseFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.GET
import java.io.IOException

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

    override fun fetchBooks(): Flow<PagingData<BookModel>> {
        return Pager(PagingConfig(pageSize = 5, enablePlaceholders = false)) {
            BookPagingSource(this)
        }.flow
    }
}

class BookPagingSource(private val booksRepository: BooksRepository) :
    PagingSource<String, BookModel>() {

    override val keyReuseSupported: Boolean = true

    override suspend fun load(params: LoadParams<String>):
            LoadResult<String, BookModel> {
        return try {
            val page = (params.key ?: "1").toInt()

            val response = booksRepository.booksFromServer(page)

            var previousKey: String? = null
            if (page > 1) {
                previousKey = (page - 1).toString()
            }

            var nextKey: String? = null
            if (page < response!!.meta.pageCount) {
                nextKey = (page + 1).toString()
            }

            LoadResult.Page(response!!.results, prevKey = previousKey, nextKey = nextKey)
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
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