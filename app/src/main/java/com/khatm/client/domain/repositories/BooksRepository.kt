package com.khatm.client.domain.repositories

import androidx.paging.PagingData
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun booksFromServer(page: Int) : Books?
    fun fetchBooks(): Flow<PagingData<BookModel>>
}