package com.movie.binged.model

data class HomeScreenSection(
    val title : String?,
    val data : List<UiPosterData>,
    val isGenreSection: Boolean = false
)