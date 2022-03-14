package com.example.user

import android.app.Application
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.user.common.BroadcastAction.ACTION_SESSION_EXPIRED
import com.example.user.common.receiver.SessionExpiredReceiver
import com.example.user.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class UserApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        registerBroadcastReceiver()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@UserApp)
            modules(
                arrayListOf(
                    appModule, viewModelModule, repositoryModule, networkModule, useCaseModule
                )
            )
        }
    }

    private fun registerBroadcastReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_SESSION_EXPIRED)
        }
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(SessionExpiredReceiver(), intentFilter)
    }
}