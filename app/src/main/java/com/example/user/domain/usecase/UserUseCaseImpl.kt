package com.example.user.domain.usecase

import com.example.user.common.extension.asErrorObject
import com.example.user.common.viewstate.ViewError
import com.example.user.data.remote.response.UserResponse
import com.example.user.data.repository.UserRepository
import com.example.user.domain.Either
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserParam

class UserUseCaseImpl(private val repository: UserRepository): UserUseCase {

    override suspend fun invoke(params: UserParam): Either<List<UserData>, ViewError> {
        return try {
            val result = repository.getUsers(params.limit, params.itemCount)
            Either.Success(result.map(userMapper))
        } catch (e: Exception) {
            Either.Fail(e.asErrorObject())
        }
    }

    private val userMapper: (UserResponse) -> UserData = { user ->
        UserData(user.id, user.username, user.photo, user.repoUrl)
    }
}