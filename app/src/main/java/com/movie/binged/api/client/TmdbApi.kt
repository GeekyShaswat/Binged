package com.movie.binged.api.client

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): TmdbMovieResponse

    @GET("tv/{id}")
    suspend fun getTvDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): TmdbTvResponse
}


data class TmdbMovieResponse(
    val id: Int,
    val title: String,
    val poster_path: String?
)

data class TmdbTvResponse(
    val id: Int,
    val name: String,
    val poster_path: String?
)
