package com.movie.binged.api.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {

    private const val BASE_URL = "https://api.trakt.tv/"
    private const val WATCHMODE_BASE_URL = "https://api.watchmode.com/v1/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .build()

    private val okHttpClient2 = OkHttpClient.Builder()
        .addInterceptor(WatchModeApiKeyInterceptor())
        .build()

    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    val watchmodeApi : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(WATCHMODE_BASE_URL)
            .client(okHttpClient2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}
