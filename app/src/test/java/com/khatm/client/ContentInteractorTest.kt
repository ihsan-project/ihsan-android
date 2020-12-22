package com.khatm.client

import androidx.paging.PagingData
import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.models.PaginationMetaData
import com.khatm.client.domain.repositories.BooksRepository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

import org.junit.Test
import org.junit.Assert.*


class ContentInteractorTest {
    @Test
    fun syncBooks() {
        class BooksRepositoryInstance : BooksRepository {

            override suspend fun booksFromServer(page: Int): Books? {
                val books = listOf(
                    BookModel(1, "slug1", "title1", 1),
                    BookModel(2, "slug2", "title2", 1)
                )
                return Books(books, meta = PaginationMetaData(count = 1, pageCount = 2, totalCount = 15))
            }

            override fun fetchBooks(): Flow<PagingData<BookModel>> {
                TODO("Not yet implemented")
            }
        }

        val booksRepsoitory = BooksRepositoryInstance()

        val contentInteractor = ContentInteractor(booksRepsoitory)

        runBlocking {
            val books = contentInteractor.getBooksAsync(1).await()

            assertEquals(2, books?.size)
            assertEquals(1, books?.first()?.id)
        }
    }
}
