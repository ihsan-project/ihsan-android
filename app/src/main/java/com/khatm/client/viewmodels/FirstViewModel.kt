package com.khatm.client.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khatm.client.ApiFactory
import com.khatm.client.models.User
import com.khatm.client.repositories.UserRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FirstViewModel : ViewModel(){

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    private val repository : UserRepository = UserRepository(ApiFactory.khatmApi)


    val userLiveData = MutableLiveData<User>()

    fun authenticate(){
        scope.launch {
            val authentication = repository.getAuthentication()
            userLiveData.postValue(authentication)
        }
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}