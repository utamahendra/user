package com.example.user.domain.usecase

import com.example.user.domain.UseCase
import com.example.user.domain.model.UserData
import com.example.user.domain.model.UserParam

interface UserUseCase: UseCase<UserParam, List<UserData>>