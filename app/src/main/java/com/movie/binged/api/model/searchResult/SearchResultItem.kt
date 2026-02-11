package com.movie.binged.api.model.searchResult

data class SearchResultItem(
    val movie: Movie,
    val show: Show,
    val type: String
)