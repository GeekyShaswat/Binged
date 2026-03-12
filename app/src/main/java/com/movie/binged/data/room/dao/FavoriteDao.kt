package com.movie.binged.data.room.dao

import androidx.room.*
import com.movie.binged.data.room.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE imdbId = :imdbId LIMIT 1")
    suspend fun getFavoriteById(imdbId: String): FavoriteEntity?
}