package com.movie.binged.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.data.room.entities.FavoriteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(private val userRepo: UserRepository) : ViewModel() {
    val favorites: StateFlow<List<FavoriteEntity>> = userRepo
        .getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class FavoritesViewModelFactory(private val userRepo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoritesViewModel(userRepo) as T
    }
}