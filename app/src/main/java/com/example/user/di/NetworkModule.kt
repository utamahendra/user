package com.example.user.di

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.user.BuildConfig
import com.example.user.common.BroadcastAction
import com.example.user.common.Timeout
import com.example.user.common.manager.HeaderInterceptor
import com.example.user.data.remote.UserApiService
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single(named("logging")) {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        } as Interceptor
    }

    single(named("header")) { HeaderInterceptor() as Interceptor }

    single {
        Authenticator { _, response ->
            val code = response.code
            val originalRequest = response.request
            val localBroadcastManager = get() as LocalBroadcastManager
            if (code == 401 || code == 403) {
                val intent = Intent(BroadcastAction.ACTION_SESSION_EXPIRED)
                localBroadcastManager.sendBroadcastSync(intent)
            }
            originalRequest
        } as Authenticator
    }

    single {
        val dispatcher = Dispatcher()
        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>(named("logging")))
            .addInterceptor(get<Interceptor>(named("header")))
            .dispatcher(dispatcher)
            .authenticator(get())
            .readTimeout(Timeout.GENERAL_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectTimeout(Timeout.GENERAL_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(get() as Gson))
            .client(get() as OkHttpClient)
            .build()
            .create(UserApiService::class.java)
    }
}
