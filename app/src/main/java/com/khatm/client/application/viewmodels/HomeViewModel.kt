package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.Deferred

class HomeViewModelFactory(
    val activity: AppCompatActivity,
    val booksRepository: BooksRepository
): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass:Class<T>): T =
        modelClass.getConstructor(
            AppCompatActivity::class.java,
            BooksRepository::class.java
        ).newInstance(activity, booksRepository)
}

class HomeViewModel(val activity: AppCompatActivity,
                    val booksRepository: BooksRepository) : ViewModelBase() {

    private val contentInteractor = ContentInteractor(activity = activity, booksRepository = booksRepository)

    fun syncBooksAsync() : Deferred<List<BookModel>?> {
        return contentInteractor.syncBooks(scope)
    }
}