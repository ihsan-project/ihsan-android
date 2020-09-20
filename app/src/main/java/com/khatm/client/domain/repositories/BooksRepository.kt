package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import kotlinx.coroutines.Deferred

interface BooksRepository {
    suspend fun booksFromServer() : Books?
    val booksFromDbAsync : Deferred<List<BookModel>?>
    fun storeToDb(books : List<BookModel>)
}