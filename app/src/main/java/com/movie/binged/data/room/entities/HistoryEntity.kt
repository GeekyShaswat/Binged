package com.movie.binged.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.movie.binged.api.model.movies_collection.Images
import com.movie.binged.api.model.movies_collection.Ids


@Entity(tableName = "watch_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val ids: Ids?,
    val title: String?,
    val images : Images?,
    val overviewText: String?,
    val mediaType: String,
    val season : Int?,
    val episode : Int?,
    val year : Int
)

