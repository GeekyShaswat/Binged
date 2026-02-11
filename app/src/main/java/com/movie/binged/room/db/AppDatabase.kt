package com.movie.binged.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.movie.binged.room.dao.FavoritesDao
import com.movie.binged.room.dao.GenreDao
import com.movie.binged.room.dao.HistoryDao
import com.movie.binged.room.dao.UserDao
import com.movie.binged.room.entities.FavoriteEntity
import com.movie.binged.room.entities.GenreEntity
import com.movie.binged.room.entities.HistoryEntity
import com.movie.binged.room.entities.MediaTypeConverter
import com.movie.binged.room.entities.UserEntity
import com.movie.binged.room.entities.UserGenreCrossRef

@Database(
    entities = [
        UserEntity::class,
        GenreEntity::class,
        UserGenreCrossRef::class,
        HistoryEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(MediaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun genreDao(): GenreDao
    abstract fun historyDao(): HistoryDao
    abstract fun favoritesDao(): FavoritesDao
}
