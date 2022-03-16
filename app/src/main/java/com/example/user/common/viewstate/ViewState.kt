package com.example.user.common.viewstate


sealed class ViewState<T> {
    data class Loading<T>(var progress: Float? = null) : ViewState<T>()
    data class Success<T>(var data: T) : ViewState<T>()
    data class Error<T>(var viewError: ViewError? = null) : ViewState<T>()
}