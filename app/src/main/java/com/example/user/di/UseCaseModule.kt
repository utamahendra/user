package com.example.user.di

import com.example.user.domain.usecase.UserUseCase
import com.example.user.domain.usecase.UserUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    factory { UserUseCaseImpl(get()) as UserUseCase }
}