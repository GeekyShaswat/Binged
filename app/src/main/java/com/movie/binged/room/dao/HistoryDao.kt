package com.movie.binged.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.movie.binged.room.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(item: HistoryEntity)

    @Query("SELECT * FROM watch_history ORDER BY id DESC")
    fun getHistory(): Flow<List<HistoryEntity>>
}
