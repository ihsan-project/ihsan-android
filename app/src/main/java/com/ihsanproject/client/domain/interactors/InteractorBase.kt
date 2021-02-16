package com.ihsanproject.client.domain.interactors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

open class InteractorBase {
    private val parentJob = Job()
    val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Default
    val scope = CoroutineScope(coroutineContext)

    fun cancelAllRequests() = coroutineContext.cancel()
}