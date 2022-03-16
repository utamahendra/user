package com.example.user.di

import com.example.user.presentation.UserListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { UserListViewModel(get(), get()) }
}