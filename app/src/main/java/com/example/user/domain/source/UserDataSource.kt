package com.example.user.domain.source

import com.example.user.data.remote.response.UserResponse

interface UserDataSource {

    suspend fun getUsers(limit: Int, itemCount: Int): List<UserResponse>
}