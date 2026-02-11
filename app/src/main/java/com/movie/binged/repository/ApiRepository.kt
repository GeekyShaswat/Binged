package com.movie.binged.repository

import com.movie.binged.api.client.ApiInterface
import com.movie.binged.api.model.movies_collection.Movie
import com.movie.binged.api.model.movies_collection.MovieCollection
import com.movie.binged.api.model.movies_collection.MovieItem
import com.movie.binged.api.model.searchResult.SearchResult
import com.movie.binged.api.model.searchResult.SearchResultItem
import com.movie.binged.api.model.seasons_episodes.ShowSeasonData
import com.movie.binged.api.model.show_collection.Show
import com.movie.binged.api.model.show_collection.ShowCollection
import com.movie.binged.api.model.show_collection.ShowItem
import retrofit2.Response

class ApiRepository(
    private val api : ApiInterface
) {


    suspend fun trendingMovies() : MovieCollection {
        return api.getTrendingMovies()
    }

    suspend fun trendingShows() : ShowCollection {
        return api.getTrendingShows()
    }

    suspend fun popularMovies() : MovieItem {
        return api.getPopularMovies()
    }

    suspend fun popularShows() : ShowItem {
        return api.getPopularShows()
    }

    suspend fun movieData( id : String) : Movie {
        return api.getMovieData(id)
    }

    suspend fun showData( id : String ) : Show {
        return api.getShowData(id)
    }

    suspend fun seasonData( id : String ) : List<ShowSeasonData> {
        return api.getSeasonData(id)
    }

    suspend fun searchResult( query : String ) : Response<List<SearchResultItem>> {
        return api.getSearch(query)
    }

}