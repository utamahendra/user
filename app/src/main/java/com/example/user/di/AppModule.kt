package com.example.user.di

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { GsonBuilder().setLenient().create() }
    single { LocalBroadcastManager.getInstance(androidContext()) }
}