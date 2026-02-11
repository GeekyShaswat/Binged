package com.movie.binged.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movie.binged.room.entities.GenreEntity

@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreEntity>
}
