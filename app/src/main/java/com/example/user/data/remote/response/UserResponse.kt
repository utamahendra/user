package com.example.user.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Long,
    @SerializedName("login") val userName: String,
    @SerializedName("avatar_url") val photo: String,
    @SerializedName("repos_url") val repoUrl: String
)