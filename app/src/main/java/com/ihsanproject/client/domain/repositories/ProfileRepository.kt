package com.ihsanproject.client.domain.repositories

import com.ihsanproject.client.domain.models.UserModel
import kotlinx.coroutines.Deferred

interface ProfileRepository {
    suspend fun authorizeWithServer(uuid: String?, email: String?, firstName: String?, idToken: String?, platform: Int?) : UserModel?
    val profileFromDbAsync : Deferred<UserModel?>
    fun storeToDb(profile : UserModel)
    fun deleteFromDb(profile: UserModel)
}

class SSOAccount(
    val id: String?,
    val email: String?,
    val displayName: String?,
    val idToken: String?
)