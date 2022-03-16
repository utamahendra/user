package com.example.user.di

import com.example.user.domain.usecase.UserDetailUseCase
import com.example.user.domain.usecase.UserDetailUseCaseImpl
import com.example.user.domain.usecase.UserUseCase
import com.example.user.domain.usecase.UserUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    factory { UserUseCaseImpl(get()) as UserUseCase }

    factory { UserDetailUseCaseImpl(get()) as UserDetailUseCase }
}