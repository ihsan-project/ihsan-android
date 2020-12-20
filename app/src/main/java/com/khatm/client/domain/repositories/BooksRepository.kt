package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.Books

interface BooksRepository {
    suspend fun booksFromServer(page: Int) : Books?
}