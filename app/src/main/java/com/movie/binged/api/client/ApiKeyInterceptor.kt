package com.movie.binged.api.client

import com.movie.binged.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .addHeader("trakt-api-key", BuildConfig.TRAKT_TV_API_KEY)
            .addHeader("trakt-api-version", "2")
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}
