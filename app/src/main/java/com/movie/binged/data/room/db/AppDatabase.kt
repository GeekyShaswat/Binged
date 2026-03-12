package com.movie.binged.data.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.movie.binged.data.room.converter.Converters
import com.movie.binged.data.room.dao.FavoriteDao
import com.movie.binged.data.room.dao.HistoryDao
import com.movie.binged.data.room.entities.FavoriteEntity
import com.movie.binged.data.room.entities.HistoryEntity

@Database(
    entities = [HistoryEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao
}
