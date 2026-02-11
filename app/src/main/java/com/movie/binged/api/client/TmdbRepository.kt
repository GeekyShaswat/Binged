package com.movie.binged.api.client

import com.movie.binged.utils.TmdbImage

class TmdbRepository {

    private val api = TmdbRetrofitClient.api

    suspend fun getMoviePoster(id: Int): String? {
        val response = api.getMovieDetails(id)
        return TmdbImage.poster(response.poster_path)
    }

    suspend fun getTvPoster(id: Int): String? {
        val response = api.getTvDetails(id)
        return TmdbImage.poster(response.poster_path)
    }
}
