package com.movie.binged.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val movieGenreId: Int?,   // TMDB movie genre id
    val seriesGenreId: Int?,  // TMDB tv genre id
    val genreName: String
)

