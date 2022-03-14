package com.example.user.di

import com.example.user.data.repository.UserRepository
import com.example.user.domain.source.UserDataSource
import org.koin.dsl.module

val repositoryModule = module {

    factory { UserRepository(get()) as UserDataSource }
}
