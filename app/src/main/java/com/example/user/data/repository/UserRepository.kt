package com.example.user.data.repository

import com.example.user.data.remote.UserApiService
import com.example.user.data.remote.response.UserResponse
import com.example.user.domain.source.UserDataSource

class UserRepository(private val apiService: UserApiService) : UserDataSource {

    override suspend fun getUsers(limit: Int, itemCount: Int): List<UserResponse> =
        apiService.getUsers(limit, itemCount)
}