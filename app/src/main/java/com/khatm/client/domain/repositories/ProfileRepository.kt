package com.khatm.client.domain.repositories

interface ProfileRepository {
    fun getProfile()
    fun getAuthentication()
    fun deleteAuthentication()
}