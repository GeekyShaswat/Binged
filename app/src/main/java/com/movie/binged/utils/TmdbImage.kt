package com.movie.binged.utils

object TmdbImage {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    fun poster(path: String?, size: String = "w500"): String? {
        return path?.let { "$BASE_URL$size$it" }
    }
}