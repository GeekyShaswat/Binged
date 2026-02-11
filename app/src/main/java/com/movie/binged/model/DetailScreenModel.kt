package com.movie.binged.model

import com.movie.binged.api.model.movies_collection.Ids
import com.movie.binged.api.model.movies_collection.Images

data class DetailScreenModel(
    val country: String,
    val ids: Ids,
    val images: Images,
    val overview: String,
    val rating: Double,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val title: String,
    val trailer: String,
    val year: Int
)
