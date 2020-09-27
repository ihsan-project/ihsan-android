package com.khatm.client

import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository

import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*


class ContentInteractorTest {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setup() {
        // Sets the given [dispatcher] as an underlying dispatcher of [Dispatchers.Main].
        // All consecutive usages of [Dispatchers.Main] will use given [dispatcher] under the hood.
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        // Resets state of the [Dispatchers.Main] to the original main dispatcher.
        // For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher to make sure no other work is running.
        mainThreadSurrogate.close()
    }

    @Test
    fun syncBooks() {
        class BooksRepositoryInstance : BooksRepository {
            override val booksFromDbAsync: Deferred<List<BookModel>?>
                get() {
                    val future = CompletableDeferred<List<BookModel>?>()

                    runBlocking {
                        future.complete(listOf(BookModel(1, "slug", "title", 1)))
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

            assertEquals(1, books?.size)
        }
    }
}
