package com.movie.binged.api.model.show_collection

import com.movie.binged.api.model.movies_collection.Colors
import com.movie.binged.api.model.movies_collection.Ids
import com.movie.binged.api.model.movies_collection.Images

data class Show(
    val aired_episodes: Int,
    val airs: Airs,
    val available_translations: List<String>,
    val certification: String,
    val colors: Colors,
    val comment_count: Int,
    val country: String,
    val first_aired: String,
    val genres: List<String>,
    val homepage: String,
    val ids: Ids,
    val images: Images,
    val language: String,
    val languages: List<String>,
    val network: String,
    val original_title: String,
    val overview: String,
    val rating: Double,
    val runtime: Int,
    val status: String,
    val subgenres: List<String>,
    val tagline: String,
    val title: String,
    val trailer: String,
    val updated_at: String,
    val votes: Int,
    val year: Int
)