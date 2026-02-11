package com.movie.binged.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.movie.binged.api.client.TmdbRepository
import com.movie.binged.api.model.searchResult.SearchResult
import com.movie.binged.api.model.searchResult.SearchResultItem
import com.movie.binged.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException

class SearchViewModel(private val apiRepository: ApiRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NOQUERY)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchResult = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResult: StateFlow<List<SearchResultItem>> = _searchResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val repository = TmdbRepository()

    private val _posterMap =
        MutableStateFlow<Map<Int, String>>(emptyMap())

    val posterMap: StateFlow<Map<Int, String>> = _posterMap


    fun loadPoster(id: Int, type: String) {
        if (_posterMap.value.containsKey(id)) return
        viewModelScope.launch {
            try {
                val poster = if (type == "movie")
                    repository.getMoviePoster(id)
                else
                    repository.getTvPoster(id)
                Log.d("TAG","poster: ${poster.toString()}")
                poster?.let {
                    _posterMap.update { current ->
                        current + (id to it)
                    }
                }
            }catch (e : kotlin.Exception){
                Log.d("TAG","got an error while poster call: ${e.message.toString()}")
            }


        }
    }
    fun getSearchData(query: String) {
        if (query.isBlank()) {
            _uiState.value = UiState.NOQUERY
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            try {
                val response = apiRepository.searchResult(query)

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()!!
                    _searchResult.value = results
                    _uiState.value = if (results.isEmpty()) {
                        _errorMessage.value = "No results found for \"$query\""
                        UiState.ERROR
                    } else {
                        UiState.SUCCESS
                    }
                } else {
                    _errorMessage.value = "Error: ${response.code()} - ${response.message()}"
                    _uiState.value = UiState.ERROR
                }
            } catch (e: SocketTimeoutException) {
                Log.e("SearchViewModel", "Timeout error", e)
                _errorMessage.value = "Connection timeout. Please check your internet."
                _uiState.value = UiState.ERROR
            } catch (e: IOException) {
                Log.e("SearchViewModel", "Network error", e)
                _errorMessage.value = "Network error. Please try again."
                _uiState.value = UiState.ERROR
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error: ${e.message}", e)
                _errorMessage.value = "Something went wrong: ${e.message}"
                _uiState.value = UiState.ERROR
            }
        }
    }

    fun clearSearch() {
        _searchResult.value = emptyList()
        _uiState.value = UiState.NOQUERY
        _errorMessage.value = null
    }
}

class SearchViewModelFactory(
    private val apiRepo: ApiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(apiRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class UiState {
    NOQUERY,
    LOADING,
    SUCCESS,
    ERROR
}