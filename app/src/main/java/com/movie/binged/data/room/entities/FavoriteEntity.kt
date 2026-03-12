package com.movie.binged.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.movie.binged.api.model.movies_collection.Ids
import com.movie.binged.api.model.movies_collection.Images

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val imdbId: String,
    val title: String?,
    val overviewText: String?,
    val mediaType: String,
    val year: Int,
    val ids: Ids?,
    val images: Images?
)