package com.movie.binged.model

import com.movie.binged.api.model.movies_collection.Ids
import com.movie.binged.api.model.movies_collection.Images

data class UiPosterData(
    val ids : Ids,
    val images : Images,
    val title : String,
    val year : Int
)