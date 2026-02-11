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
import com.movie.binged.repository.ApiRepository
import com.movie.binged.utils.NetworkMonitor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import java.net.SocketTimeoutException

class HomeViewModel(
    private val apiRepo: ApiRepository,
    context: Context
) : ViewModel() {

    val networkMonitor = NetworkMonitor(context)
    private var _homeSection = MutableStateFlow< List<HomeScreenSection>>(emptyList())
    var homeSection : StateFlow<List<HomeScreenSection>> = _homeSection

    private var _uiState = MutableStateFlow<Status>(Status.Loading)
    var uiState : StateFlow<Status> = _uiState
    
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun loadTrendingData(){
        _uiState.value = Status.Loading
        if (!networkMonitor.isInternetAvailable()) {
            _uiState.value = Status.Error("Network Not Available")
            return
        }
        viewModelScope.launch {
            try {
                val trendingMovieDef = async { apiRepo.trendingMovies() }
                val trendingShowDef = async { apiRepo.trendingShows() }
                val popularMovieDef = async { apiRepo.popularMovies() }
                val popularShowDef = async { apiRepo.popularShows() }

                val sectionList = listOf(
                    HomeScreenSection(
                        "Trending Movies",
                        trendingMovieDef.await().map { it.movie.toPosterUI() },
                        "movie"
                    ),
                    HomeScreenSection(
                        "Trending Shows",
                        trendingShowDef.await().map { it.show.toPosterUI() },
                        "show"
                    ),
                    HomeScreenSection(
                        "Popular Movies",
                        popularMovieDef.await().map { it.toPosterUI() },
                        "movie"
                    ),
                    HomeScreenSection(
                        "Popular Shows",
                        popularShowDef.await().map { it.toPosterUI() },
                        "show"
                    )
                )

                _homeSection.value = sectionList
                _uiState.value = Status.Success
                Log.d("TAG"," the final section list : ${_homeSection.value}")
            }
            catch (e: IOException) {
                _uiState.value = Status.Error("Connection timeout. Please check your internet.")
                Log.e("DetailsViewModel", "Timeout error", e)
            }
            catch (e : Exception){
                Log.d("TAG"," caused error : ${e.message}")
                _uiState.value = Status.Error(e.message.toString())
            }

        }
    }
    private fun Movie.toPosterUI() : UiPosterData {
        return UiPosterData(
            ids = this.ids,
            images = images,
            title = title,
            year = year
        )
    }
    private fun Show.toPosterUI() : UiPosterData {
        return UiPosterData(
            ids = this.ids,
            images = images,
            title = title,
            year = year
        )
    }
}
class HomeViewModelFactory(
    val apiRepo : ApiRepository,
    val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(apiRepo, context) as T
        }
        throw IllegalArgumentException("unknown VM")
    }
}

sealed class Status() {
    object Loading : Status()
    object Success : Status()
    data class Error( val message : String) : Status()
}