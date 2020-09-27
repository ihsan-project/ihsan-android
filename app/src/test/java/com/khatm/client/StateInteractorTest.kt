package com.khatm.client

import com.khatm.client.domain.interactors.ContentInteractor
import com.khatm.client.domain.interactors.StateInteractor
import com.khatm.client.domain.models.*
import com.khatm.client.domain.repositories.BooksRepository
import com.khatm.client.domain.repositories.ProfileRepository
import com.khatm.client.domain.repositories.SettingsRepository
import com.khatm.client.repositoryInstances.BooksRepositoryInstance

import kotlinx.coroutines.*

import org.junit.Test
import org.junit.Assert.*


class StateInteractorTest {
    @Test
    fun syncSettings() {
        class SettingsRepositoryInstance : SettingsRepository {
            override val settingsFromDbAsync: Deferred<SettingsModel?>
                get() = TODO("Not yet implemented")

            override suspend fun settingsFromServer(currentVersion: Int): SettingsModel? {
                return SettingsModel(1, Constants())
            }

            override fun storeToDb(settings: SettingsModel) {
                TODO("Not yet implemented")
            }
        }
        class ProfileRepositoryInstance : ProfileRepository {
            override val profileFromDbAsync: Deferred<UserModel?>
                get() = TODO("Not yet implemented")

            override fun deleteFromDb(profile: UserModel) {
                TODO("Not yet implemented")
            }

            override fun storeToDb(profile: UserModel) {
                TODO("Not yet implemented")
            }

            override suspend fun authorizeWithServer(
                uuid: String?,
                email: String?,
                firstName: String?,
                idToken: String?,
                platform: Int?
            ): UserModel? {
                TODO("Not yet implemented")
            }
        }

        val settingsRepository = SettingsRepositoryInstance()
        val profileRepository = ProfileRepositoryInstance()

        val stateInteractor = StateInteractor(settingsRepository, profileRepository)

        runBlocking {
            val settings = stateInteractor.syncSettingsAsync().await()

            assertEquals(2, books?.size)
            assertEquals(1, books?.first()?.id)
        }
    }
}
