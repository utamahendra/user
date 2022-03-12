package com.example.user.data.remote

import com.example.user.data.remote.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(
        @Query("per_page") limit: Int,
        @Query("since") itemCount: Int
    ): List<UserResponse>
}