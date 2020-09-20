package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.Books
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HomeViewModelFactory(
    val activity: AppCompatActivity,
    val booksRepository: BooksRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create(modelClass:Class<T>): T {
        return HomeViewModelFactory(activity, booksRepository) as T
    }
}

class HomeViewModel(val activity: AppCompatActivity,
                    val booksRepository: BooksRepository) : ViewModel() {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val contentInteractor = ContentInteractor(activity = activity, booksRepository = booksRepository)

    fun syncBooksAsync() : Deferred<Books?> {
        return contentInteractor.syncBooks(scope)
    }
}