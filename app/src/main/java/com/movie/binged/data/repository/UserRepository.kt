package com.movie.binged.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.movie.binged.data.room.dao.FavoriteDao
import com.movie.binged.data.room.dao.HistoryDao
import com.movie.binged.data.room.entities.FavoriteEntity
import com.movie.binged.data.room.entities.HistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val historyDao: HistoryDao,
    private val favoriteDao: FavoriteDao,
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        val NAME_KEY   = stringPreferencesKey("user_name")
        val GENRES_KEY = stringSetPreferencesKey("user_genres")
    }

    suspend fun saveProfile(name: String, genres: List<String>) {
        dataStore.edit { prefs ->
            prefs[NAME_KEY]   = name
            prefs[GENRES_KEY] = genres.toSet()
        }
    }

    suspend fun saveGenres(genres: List<String>) {
        dataStore.edit { prefs ->
            prefs[GENRES_KEY] = genres.toSet()
        }
    }

    val profileFlow: Flow<Pair<String, List<String>>> = dataStore.data.map { prefs ->
        Pair(
            prefs[NAME_KEY] ?: "",
            prefs[GENRES_KEY]?.toList() ?: emptyList()
        )
    }

    fun returnHistory() : Flow<List<HistoryEntity>> { return historyDao.getHistory() }

    suspend fun insert(historyItem : HistoryEntity){
        historyDao.addToHistory(historyItem)
    }

    suspend fun getHistoryOnce(): List<HistoryEntity> {
        return historyDao.getHistoryOnce()
    }

    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    suspend fun isFavorite(imdbId: String): Boolean =
        favoriteDao.getFavoriteById(imdbId) != null

    suspend fun toggleFavorite(entity: FavoriteEntity): Boolean {
        val existing = favoriteDao.getFavoriteById(entity.imdbId)
        return if (existing != null) {
            favoriteDao.deleteFavorite(existing)
            false
        } else {
            favoriteDao.insertFavorite(entity)
            true
        }
    }
    suspend fun clearProfile() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
