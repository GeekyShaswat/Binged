package com.movie.binged.api.model.seasons_episodes

import com.google.gson.annotations.SerializedName
import com.movie.binged.api.model.movies_collection.Ids

data class ShowSeasonData(
    val ids: Ids,
    val number: Int,
    @SerializedName("aired_episodes")
    val airedEpisodes: Int,
    @SerializedName("first_aired")
    val firstAired: String
)
