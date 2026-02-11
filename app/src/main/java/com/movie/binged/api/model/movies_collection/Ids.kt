package com.movie.binged.api.model.movies_collection

data class Ids(
    val imdb: String,
    val plex: Plex,
    val slug: String,
    val tmdb: Int,
    val trakt: Int
)