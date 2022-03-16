package com.example.user.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.user.common.viewstate.PaginationViewState
import com.example.user.common.viewstate.ViewState
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserDetailData
import com.example.user.domain.model.UserParam
import com.example.user.domain.usecase.UserDetailUseCase
import com.example.user.domain.usecase.UserUseCase
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userUseCase: UserUseCase,
    private val userDetailUseCase: UserDetailUseCase
) : ViewModel() {

    companion object {
        const val USER_LIMIT = 10
    }

    val userState = MutableLiveData<PaginationViewState<List<UserData>>>()

    val userDetailState = MutableLiveData<ViewState<UserDetailData>>()

    var users = mutableListOf<UserData>()

    fun getUserDetail(username: String) {
        viewModelScope.launch {
            userDetailState.postValue(ViewState.Loading())
            userDetailUseCase.invoke(username).handleResult({ userDetailData ->
                userDetailState.postValue(ViewState.Success(userDetailData))
            }, { viewError ->
                userDetailState.postValue(ViewState.Error(viewError))
            })
        }
    }

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
                    userState.postValue(
                        PaginationViewState.Success(
                            users,
                            (users.isEmpty() || users.size < USER_LIMIT)
                        )
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