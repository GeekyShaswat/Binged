package com.movie.binged.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.binged.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepo: UserRepository) : ViewModel() {

    val profileFlow: Flow<Pair<String, List<String>>> = userRepo.profileFlow

    fun saveProfile(name: String, genres: List<String>) {
        viewModelScope.launch {
            userRepo.saveProfile(name, genres)
        }
    }

    fun saveGenres(genres: List<String>) {
        viewModelScope.launch {
            userRepo.saveGenres(genres)
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            userRepo.clearProfile()
            onDone()
        }
    }
}

class UserViewModelFactory(private val userRepo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserViewModel(userRepo) as T
    }
}