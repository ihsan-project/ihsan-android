package com.khatm.client.domain.interactors

import androidx.appcompat.app.AppCompatActivity
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

class ContentInteractor(val activity: AppCompatActivity, val booksRepository: BooksRepository) {

    fun syncBooks(scope: CoroutineScope) : Deferred<Books?> {
        val future = CompletableDeferred<Books?>()

        scope.launch {
            val books = booksRepository.booksFromServer()

            books?.let {
                booksRepository.storeToDbAsync(books = it.books)
            }

            future.complete(books)
        }

        return future
    }
}