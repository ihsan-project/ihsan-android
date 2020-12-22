package com.khatm.client.application.viewmodels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.models.BookModel
import com.khatm.client.domain.repositories.BooksRepository
import kotlinx.coroutines.flow.collectLatest

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

    private val contentInteractor = ContentInteractor(booksRepository)

    suspend fun onPage(block: suspend (PagingData<BookModel>) -> Unit) {
        contentInteractor.fetchBooks().collectLatest {
            block(it)
        }
    }
}