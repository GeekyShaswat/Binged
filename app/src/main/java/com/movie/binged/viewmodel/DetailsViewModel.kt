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
import com.movie.binged.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel(val apiRepo: ApiRepository) : ViewModel() {

    private val _dataObject = MutableStateFlow<DetailScreenModel?>(null)
    var dataObject : StateFlow<DetailScreenModel?> = _dataObject

    private val _seasonData = MutableStateFlow<List<SeasonEpModel>>(emptyList())
    var seasonData : StateFlow<List<SeasonEpModel>> = _seasonData

    fun loadData(id: String?, type: String?) {
        try {
            viewModelScope.launch {

                if (type.equals("movie")) {
                    val results = apiRepo.movieData(id ?: "")
                    _dataObject.value = results.toDetailsUI()
                }
                else {
                   val results =  apiRepo.showData(id ?: "")
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

                _seasonData.value = seasonData.map { showSeasonData ->
                   showSeasonData.toUI()
                }
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

class DetailViewModelFactory( val apiRepo : ApiRepository ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailsViewModel::class.java)){
            return DetailsViewModel(apiRepo) as T
        }
        throw IllegalArgumentException("unknown VM")
    }
}