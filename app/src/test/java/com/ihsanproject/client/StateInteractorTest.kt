package com.ihsanproject.client

import com.ihsanproject.client.domain.interactors.StateInteractor
import com.ihsanproject.client.domain.models.*
import com.ihsanproject.client.domain.repositories.ProfileRepository
import com.ihsanproject.client.domain.repositories.SSOAccount
import com.ihsanproject.client.domain.repositories.SettingsRepository

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

    @Test
    fun isNotLoggedIn() {
        class SettingsRepositoryInstance : SettingsRepository {
            override val settingsFromDbAsync: Deferred<SettingsModel?>
                get() = TODO("Not yet implemented")

            override suspend fun settingsFromServer(currentVersion: Int): SettingsModel? {
                TODO("Not yet implemented")
            }

            override fun storeToDb(settings: SettingsModel) {
                TODO("Not yet implemented")
            }
        }
        class ProfileRepositoryInstance : ProfileRepository {
            override val profileFromDbAsync: Deferred<UserModel?>
                get() {
                    val future = CompletableDeferred<UserModel?>()

                    future.complete(null)

                    return future
                }

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
            val isLoggedIn = stateInteractor.loginState.await()

            assertEquals(false, isLoggedIn)
        }
    }

    @Test
    fun isLoggedIn() {
        class SettingsRepositoryInstance : SettingsRepository {
            override val settingsFromDbAsync: Deferred<SettingsModel?>
                get() = TODO("Not yet implemented")

            override suspend fun settingsFromServer(currentVersion: Int): SettingsModel? {
                TODO("Not yet implemented")
            }

            override fun storeToDb(settings: SettingsModel) {
                TODO("Not yet implemented")
            }
        }
        class ProfileRepositoryInstance : ProfileRepository {
            override val profileFromDbAsync: Deferred<UserModel?>
                get() {
                    val future = CompletableDeferred<UserModel?>()

                    val mockUser = Mockito.mock(UserModel::class.java)
                    Mockito.`when`(mockUser.access).thenReturn("someValue")

                    future.complete(mockUser)

                    return future
                }

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
            val isLoggedIn = stateInteractor.loginState.await()

            assertEquals(true, isLoggedIn)
        }
    }

    @Test
    fun syncAuthentication() {
        class SettingsRepositoryInstance : SettingsRepository {
            override val settingsFromDbAsync: Deferred<SettingsModel?>
                get() {
                    val future = CompletableDeferred<SettingsModel?>()

                    val mockSettings = Mockito.mock(SettingsModel::class.java)

                    future.complete(mockSettings)

                    return future
                }

            override suspend fun settingsFromServer(currentVersion: Int): SettingsModel? {
                TODO("Not yet implemented")
            }

            override fun storeToDb(settings: SettingsModel) {
                TODO("Not yet implemented")
            }
        }
        class ProfileRepositoryInstance : ProfileRepository {
            override val profileFromDbAsync: Deferred<UserModel?>
                get() {
                    val future = CompletableDeferred<UserModel?>()

                    val mockUser = Mockito.mock(UserModel::class.java)

                    future.complete(mockUser)

                    return future
                }

            override fun deleteFromDb(profile: UserModel) {
                TODO("Not yet implemented")
            }

            override fun storeToDb(profile: UserModel) {

            }

            override suspend fun authorizeWithServer(
                uuid: String?,
                email: String?,
                firstName: String?,
                idToken: String?,
                platform: Int?
            ): UserModel? {
                val mockUser = Mockito.mock(UserModel::class.java)
                Mockito.`when`(mockUser.id).thenReturn(3)

                return mockUser
            }
        }

        val settingsRepository = SettingsRepositoryInstance()
        val profileRepository = ProfileRepositoryInstance()

        val stateInteractor = StateInteractor(settingsRepository, profileRepository)

        runBlocking {
            val mockAccount = Mockito.mock(SSOAccount::class.java)
            Mockito.`when`(mockAccount.id).thenReturn("SSOId")

            val user = stateInteractor.syncAuthenticationAsync(mockAccount).await()

            assertEquals(3, user?.id)
        }
    }

}
