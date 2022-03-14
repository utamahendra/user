package com.example.user.common

object ErrorCode {
    const val GLOBAL_INTERNET_ERROR = "GENERAL_INTERNET_ERROR"
    const val GLOBAL_UNKNOWN_ERROR = "GLOBAL_UNKNOWN_ERROR"
}

object Timeout {
    const val GENERAL_TIMEOUT: Long = 20 * 1000
}

object BroadcastAction {
    const val ACTION_SESSION_EXPIRED = "ACTION_SESSION_EXPIRED"
}