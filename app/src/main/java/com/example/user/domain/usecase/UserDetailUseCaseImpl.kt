package com.example.user.domain.usecase

import com.example.user.common.extension.asErrorObject
import com.example.user.common.viewstate.ViewError
import com.example.user.data.remote.response.UserDetailResponse
import com.example.user.domain.Either
import com.example.user.domain.model.UserDetailData
import com.example.user.domain.source.UserDataSource

class UserDetailUseCaseImpl(private val repository: UserDataSource) : UserDetailUseCase {

    override suspend fun invoke(params: String): Either<UserDetailData, ViewError> {
        return try {
            val result = repository.getUserDetail(params)
            Either.Success(userMapper(result))
        } catch (e: Exception) {
            Either.Fail(e.asErrorObject())
        }
    }

    private val userMapper: (UserDetailResponse) -> UserDetailData = { user ->
        UserDetailData(user.name.orEmpty(), user.email.orEmpty(), user.createdAt.orEmpty())
    }
}