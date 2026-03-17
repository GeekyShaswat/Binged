package com.movie.binged.viewmodel

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.binged.api.model.movies_collection.Movie
import com.movie.binged.api.model.show_collection.Show
import com.movie.binged.model.HomeScreenSection
import com.movie.binged.model.UiPosterData
import com.movie.binged.data.repository.ApiRepository
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.data.room.entities.HistoryEntity
import com.movie.binged.utils.NetworkMonitor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

class HomeViewModel(
    private val apiRepo: ApiRepository,
    context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    val networkMonitor = NetworkMonitor(context)
    private var _homeSection = MutableStateFlow< List<HomeScreenSection>>(emptyList())
    var homeSection : StateFlow<List<HomeScreenSection>> = _homeSection

    private var _uiState = MutableStateFlow<Status>(Status.Loading)
    var uiState : StateFlow<Status> = _uiState
    private var loadedForGenres: List<String>? = null

    // ← Always live — updates automatically when DB changes
    val historySection: StateFlow<HomeScreenSection> = userRepository.returnHistory()
        .map { historyItems ->
            HomeScreenSection(
                title = "Previously Watched",
                data = historyItems.map { it.toPosterUI(it.mediaType) }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeScreenSection("Previously Watched", emptyList())
        )
    fun loadTrendingData(userGenres: List<String> = emptyList()) {
        if (loadedForGenres == userGenres) return
        loadedForGenres = userGenres
        _uiState.value = Status.Loading
        if (!networkMonitor.isInternetAvailable()) {
            _uiState.value = Status.Error("Network Not Available")
            return
        }
        viewModelScope.launch {
            try {
                val trendingMovieDef = async { apiRepo.trendingMovies() }
                val trendingShowDef  = async { apiRepo.trendingShows() }
                val popularMovieDef  = async { apiRepo.popularMovies() }
                val popularShowDef   = async { apiRepo.popularShows() }

                val genreMovieDefs = userGenres.associateWith { genre ->
                    async { runCatching { apiRepo.moviesByGenre(genre) }.getOrElse { emptyList() } }
                }

                // ← No historyItems fetch here anymore
                val sectionList = mutableListOf(
                    HomeScreenSection(
                        "Trending Movies",
                        trendingMovieDef.await().map { it.movie.toPosterUI() }
                    ),
                    HomeScreenSection(
                        "HISTORY_PLACEHOLDER",
                        emptyList()
                    ),
                    HomeScreenSection(
                        "Trending Shows",
                        trendingShowDef.await().map { it.show.toPosterUI() }
                    ),
                    HomeScreenSection(
                        "Popular Movies",
                        popularMovieDef.await().map { it.toPosterUI() }
                    ),
                    HomeScreenSection(
                        "Popular Shows",
                        popularShowDef.await().map { it.toPosterUI() }
                    )
                )

                if (userGenres.isNotEmpty()) {
                    userGenres.forEach { genre ->
                        val movies = genreMovieDefs[genre]?.await() ?: emptyList()
                        if (movies.isNotEmpty()) {
                            sectionList.add(
                                HomeScreenSection(
                                    title = genre,
                                    data = movies.map { it.movie.toPosterUI() },
                                    isGenreSection = true
                                )
                            )
                        }
                    }
                }

                _homeSection.value = sectionList
                _uiState.value = Status.Success

            } catch (e: IOException) {
                _uiState.value = Status.Error("Connection timeout.")
            } catch (e: Exception) {
                _uiState.value = Status.Error(e.message.toString())
            }
        }
    }

    private fun HistoryEntity.toPosterUI(type : String) : UiPosterData {
        return UiPosterData(
            ids = this.ids,
            images = this.images,
            title = this.title,
            year = this.year,
            type = type
        )

    }
    private fun Movie.toPosterUI() : UiPosterData {
        return UiPosterData(
            ids = this.ids,
            images = images,
            title = title,
            year = year,
            type = "movie"
        )
    }
    private fun Show.toPosterUI() : UiPosterData {
        return UiPosterData(
            ids = this.ids,
            images = images,
            title = title,
            year = year,
            type = "show"
        )
    }
}
class HomeViewModelFactory(
    val apiRepo : ApiRepository,
    val context: Context,
    val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(apiRepo, context, userRepository) as T
        }
        throw IllegalArgumentException("unknown VM")
    }
}

sealed class Status() {
    object Loading : Status()
    object Success : Status()
    data class Error( val message : String) : Status()
}