package com.example.user.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    val name: String?,
    val email: String?,
    @SerializedName("created_at") val createdAt: String?
)