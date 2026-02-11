package com.movie.binged.api.model.movies_collection

data class Images(
    val banner: List<String>,
    val clearart: List<String>,
    val fanart: List<String>,
    val logo: List<String>,
    val poster: List<String>,
    val thumb: List<String>
)