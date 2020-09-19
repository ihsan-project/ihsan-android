package com.khatm.client.domain.repositories

interface BooksRepository {
    fun listBooks()
    fun writeBooks()
    fun getBook()
}