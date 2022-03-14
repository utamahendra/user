package com.example.user.common.manager

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .addHeader("Accept", "application/vnd.github.v3+json")

        return chain.proceed(requestBuilder.build())

    }
}
