package com.example.user.common.viewstate


data class ViewError(
    val errorCode: String,
    val message: String? = null,
    val data: Any? = null
)