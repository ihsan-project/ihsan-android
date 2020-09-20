package com.khatm.client.domain.repositories

import com.khatm.client.domain.models.UserModel
import kotlinx.coroutines.Deferred

interface ProfileRepository {
    suspend fun authorizeWithServer(uuid: String?, email: String?, firstName: String?, idToken: String?, platform: Int?) : UserModel?
    val profileFromDbAsync : Deferred<UserModel?>
    fun storeToDb(profile : UserModel)
    fun deleteFromDb(profile: UserModel)
}