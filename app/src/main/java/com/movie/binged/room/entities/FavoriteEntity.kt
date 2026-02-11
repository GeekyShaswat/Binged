package com.movie.binged.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val tmdbId: Int,
    val name: String,
    val posterPath: String?,
    val backdropPaht : String?,
    val overviewText: String?,
    val mediaType: MediaType
)
