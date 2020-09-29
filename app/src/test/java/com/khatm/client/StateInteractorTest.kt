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
import org.mockito.Mockito


class StateInteractorTest {
    @Test
    fun syncSettings() {
        class SettingsRepositoryInstance : SettingsRepository {
            override val settingsFromDbAsync: Deferred<SettingsModel?>
                get() {
                    val future = CompletableDeferred<SettingsModel?>()

                    future.complete(null)

                    return future
                }

            override suspend fun settingsFromServer(currentVersion: Int): SettingsModel? {
                val mockConstant = Mockito.mock(Constants::class.java)
                val mockFeatures = Mockito.mock(Features::class.java)

                return SettingsModel(1, mockConstant, mockFeatures)
            }

            override fun storeToDb(settings: SettingsModel) {

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

            assertEquals(1, settings?.version)
        }
    }
}
