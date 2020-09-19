package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.ContentRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class ChallengeViewModel() : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)
    private lateinit var activity: AppCompatActivity

    private lateinit var contentRepository: ContentRepository

    fun setupFor(authActivity: AppCompatActivity) {
        activity = authActivity
        contentRepository = ContentRepository(activity.application, scope)
    }

    fun getBooksFromServerAsync() : Deferred<Books?> {
        val future = CompletableDeferred<Books?>()

        scope.launch {
            val books = contentRepository.getBooksFromServer()

            future.complete(books)
        }

        return future
    }

    val allBooksAsync : Deferred<List<BookModel>?>
        get() {
            val future = CompletableDeferred<List<BookModel>?>()

            // Dispatch to main thread: https://stackoverflow.com/a/54090499
            GlobalScope.launch(Dispatchers.Main) {
                contentRepository.books?.observe(activity, Observer {
                    future.complete(it)
                })
            }

            return future
        }

    fun storeBooksAsync(books: List<BookModel>) : Deferred<Boolean> {
        return contentRepository.store(books)
    }
}