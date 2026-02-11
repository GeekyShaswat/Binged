package com.movie.binged.repository

import androidx.room.Transaction
import com.movie.binged.room.dao.UserDao
import com.movie.binged.room.dao.UserGenreDao
import com.movie.binged.room.entities.GenreEntity
import com.movie.binged.room.entities.UserEntity
import com.movie.binged.room.entities.UserGenreCrossRef

class UserRepository(
    private val userDao: UserDao,
    private val userGenreDao: UserGenreDao
) {

    @Transaction
    suspend fun saveUserWithGenres(
        userName: String,
        selectedGenres: List<GenreEntity>
    ) {
        val userId = userDao.insertUser(
            UserEntity(name = userName)
        )

        val refs = selectedGenres.map {
            UserGenreCrossRef(
                userId = userId,
                genreId = it.id
            )
        }

        userGenreDao.insertUserGenreRefs(refs)
    }
}
