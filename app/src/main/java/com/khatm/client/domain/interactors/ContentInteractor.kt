package com.khatm.client.domain.interactors

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContentInteractor(private val booksRepository: BooksRepository) : InteractorBase() {

    fun getBooksAsync(page: Int) : Deferred<List<BookModel>?> {
        val future = CompletableDeferred<List<BookModel>?>()

        scope.launch {
            var books = booksRepository.booksFromServer(page)

            future.complete(books?.results)
        }

        return future
    }

    fun fetchBooks(): Flow<PagingData<BookModel>> {
        return booksRepository.fetchBooks().cachedIn(scope)
    }
}