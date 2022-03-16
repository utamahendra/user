package com.example.user.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.user.common.viewstate.PaginationViewState
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserParam
import com.example.user.domain.usecase.UserUseCase
import kotlinx.coroutines.launch

class UserListViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    companion object {
        const val USER_LIMIT = 10
    }

    val userState = MutableLiveData<PaginationViewState<List<UserData>>>()

    var users = mutableListOf<UserData>()

    fun getUsers(itemCount: Int = 0) {
        viewModelScope.launch {
            userState.postValue(if (itemCount > 0) PaginationViewState.LoadMoreLoading() else PaginationViewState.Loading())
            userUseCase.invoke(UserParam(USER_LIMIT, itemCount)).handleResult({
                if (itemCount == 0) {
                    users = it.toMutableList()
                } else {
                    users.addAll(it)
                }

                if (users.isEmpty()) userState.postValue(
                    PaginationViewState.EmptyData(Unit)
                ) else {
                    userState.postValue(PaginationViewState.Success(
                        users,
                        (users.isEmpty() || users.size < USER_LIMIT))
                    )
                }
            },
                {
                    if (itemCount == 0) {
                        userState.postValue(PaginationViewState.Error(it))
                    } else {
                        userState.postValue(PaginationViewState.PaginationError(it))
                    }
                })

        }
    }
}