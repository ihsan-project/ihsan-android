package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import kotlinx.coroutines.Deferred

interface BooksRepository {
    suspend fun booksFromServer() : Books?
    fun storeToDbAsync(books : List<BookModel>) : Deferred<Boolean>
}