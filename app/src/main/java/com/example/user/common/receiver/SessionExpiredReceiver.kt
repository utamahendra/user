package com.example.user.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SessionExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        with(context) {
            Toast.makeText(context, "Error 401/403", Toast.LENGTH_LONG).show()
        }
    }

}