package com.movie.binged.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.movie.binged.room.entities.UserEntity
import com.movie.binged.room.entities.UserWithGenres

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithGenres(userId: Long): UserWithGenres
}
