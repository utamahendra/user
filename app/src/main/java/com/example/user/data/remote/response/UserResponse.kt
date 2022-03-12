package com.example.user.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val photo: String,
    @SerializedName("repos_url") val repoUrl: String
)