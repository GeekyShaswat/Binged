package com.movie.binged.api.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbRetrofitClient {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(TmdbApiKeyInterceptor())
            .build()
    }

    val api: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApi::class.java)
    }
}
