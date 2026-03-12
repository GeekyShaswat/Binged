package com.movie.binged.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.binged.api.model.movies_collection.Movie
import com.movie.binged.api.model.seasons_episodes.ShowSeasonData
import com.movie.binged.api.model.show_collection.Show
import com.movie.binged.model.DetailScreenModel
import com.movie.binged.model.SeasonEpModel
import com.movie.binged.data.repository.ApiRepository
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.data.room.entities.FavoriteEntity
import com.movie.binged.data.room.entities.HistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailsViewModel(val apiRepo: ApiRepository, val userRepository: UserRepository) : ViewModel() {

    private val _dataObject = MutableStateFlow<DetailScreenModel?>(null)
    var dataObject : StateFlow<DetailScreenModel?> = _dataObject

    private val _seasonData = MutableStateFlow<List<SeasonEpModel>>(emptyList())
    var seasonData : StateFlow<List<SeasonEpModel>> = _seasonData

    private val _savedSeason = MutableStateFlow<Int?>(null)
    val savedSeason: StateFlow<Int?> = _savedSeason

    private val _savedEpisode = MutableStateFlow<Int?>(null)
    val savedEpisode: StateFlow<Int?> = _savedEpisode

    private val _historyChecked = MutableStateFlow(false)
    val historyChecked: StateFlow<Boolean> = _historyChecked

    // Add to DetailsViewModel

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun checkFavorite(imdbId: String) {
        viewModelScope.launch {
            _isFavorite.value = userRepository.isFavorite(imdbId)
        }
    }

    fun toggleFavorite(entity: FavoriteEntity) {
        viewModelScope.launch {
            _isFavorite.value = userRepository.toggleFavorite(entity)
        }
    }

    fun loadHistoryFor(imdb: String) {
        viewModelScope.launch {
            val history = userRepository.getHistoryOnce()
                .find { it.ids?.imdb == imdb }
            _savedSeason.value = history?.season
            _savedEpisode.value = history?.episode
            _historyChecked.value = true   // ← signal done
        }
    }
    fun loadData(id: String?, type: String?) {
        try {
            viewModelScope.launch {

                if (type.equals("movie")) {
                    val results = apiRepo.movieData(id ?: "")
                    _dataObject.value = results.toDetailsUI()
                }
                else {
                   val results =  apiRepo.showData(id ?: "")
                    Log.d("TAG", "result for show :$results")
                    _dataObject.value = results.toDetailsUI()
                }

                Log.d("TAG","data object for details screen: ${_dataObject.value}")
            }
        } catch (e: Exception) {
            Log.d("TAG", "the error caused by : ${e.message}")
        }
    }

    fun getSeasonData( id: String? ){
        try {
            viewModelScope.launch {
                val seasonData = apiRepo.seasonData(id ?: "null")
                _seasonData.value = seasonData.map { it.toUI() }
            }
        }catch (e : Exception){
            Log.d("TAG"," message for exception: ${e.message}")
        }
    }

    private fun ShowSeasonData.toUI() : SeasonEpModel {
        return SeasonEpModel(
            this.number,
            this.airedEpisodes
        )
    }
    fun insertHistory(item : HistoryEntity){
        viewModelScope.launch {
            val historyItems = userRepository.returnHistory().first()
            val exist = historyItems.any{ it.ids?.tmdb == item.ids?.tmdb}
            if (!exist) {
                userRepository.insert(item)
                Log.d("History","new element added: ${item}")
            }

        }
    }

    private fun Movie.toDetailsUI() : DetailScreenModel {
        return DetailScreenModel(
             country,
             ids,
             images,
            overview ,
            rating ,
            runtime,
            status ,
            tagline,
            title ,
            trailer,
            year
        )
    }

    private fun Show.toDetailsUI() : DetailScreenModel {
        return DetailScreenModel(
            country,
            ids,
            images,
            overview ,
            rating ,
            runtime,
            status ,
            tagline,
            title ,
            trailer,
            year
        )
    }
}

class DetailViewModelFactory( val apiRepo : ApiRepository, val userRepository: UserRepository ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailsViewModel::class.java)){
            return DetailsViewModel(apiRepo, userRepository) as T
        }
        throw IllegalArgumentException("unknown VM")
    }
}