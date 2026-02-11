package com.movie.binged.api.client

import com.movie.binged.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class WatchModeApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("apiKey", BuildConfig.WATCHMODE_API_KEY)
            .build()

        return chain.proceed(newRequest)
    }
}