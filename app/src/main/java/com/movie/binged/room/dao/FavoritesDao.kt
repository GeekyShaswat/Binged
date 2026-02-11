package com.movie.binged.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movie.binged.room.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(item: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(item: FavoriteEntity)

    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<List<FavoriteEntity>>
}
