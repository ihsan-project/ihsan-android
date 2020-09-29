package com.khatm.client

import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository

import kotlinx.coroutines.*

import org.junit.Test
import org.junit.Assert.*


class ContentInteractorTest {
    @Test
    fun syncBooks() {
        class BooksRepositoryInstance : BooksRepository {
            override val booksFromDbAsync: Deferred<List<BookModel>?>
                get() {
                    val future = CompletableDeferred<List<BookModel>?>()

                    runBlocking {
                        future.complete(listOf(
                            BookModel(1, "slug1", "title1", 1),
                            BookModel(2, "slug2", "title2", 1)
                        ))
                    }

                    return future
                }

            override suspend fun booksFromServer(): Books? {
                TODO("Not yet implemented")
            }

            override fun storeToDb(books: List<BookModel>) {
                TODO("Not yet implemented")
            }
        }

        val booksRepsoitory = BooksRepositoryInstance()

        val contentInteractor = ContentInteractor(booksRepsoitory)

        runBlocking {
            val books = contentInteractor.syncBooksAsync().await()

            assertEquals(2, books?.size)
            assertEquals(1, books?.first()?.id)
        }
    }
}
