package com.movie.binged.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.movie.binged.room.entities.UserGenreCrossRef

@Dao
interface UserGenreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGenreRefs(refs: List<UserGenreCrossRef>)
}
