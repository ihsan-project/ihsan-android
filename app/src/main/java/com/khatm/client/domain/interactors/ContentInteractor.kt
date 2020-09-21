package com.khatm.client.domain.interactors

import androidx.appcompat.app.AppCompatActivity
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

class ContentInteractor(private val activity: AppCompatActivity,
                        private val scope: CoroutineScope,
                        private val booksRepository: BooksRepository) {

    fun syncBooks() : Deferred<List<BookModel>?> {
        val future = CompletableDeferred<List<BookModel>?>()

        scope.launch {
            var books = booksRepository.booksFromDbAsync.await()

            if (books == null) {
                val books = booksRepository.booksFromServer()

                books?.let {
                    booksRepository.storeToDb(books = it.books)
                }
            }

            future.complete(books)
        }

        return future
    }
}