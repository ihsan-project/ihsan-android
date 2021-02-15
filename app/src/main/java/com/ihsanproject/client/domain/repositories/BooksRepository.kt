package com.ihsanproject.client.domain.repositories

import androidx.paging.PagingData
import com.ihsanproject.client.domain.models.BookModel
import com.ihsanproject.client.domain.models.Books
import kotlinx.coroutines.flow.Flow

interface BooksRepository {
    suspend fun booksFromServer(page: Int) : Books?
    fun fetchBooks(): Flow<PagingData<BookModel>>
}