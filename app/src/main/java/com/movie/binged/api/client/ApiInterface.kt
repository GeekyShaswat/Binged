package com.movie.binged.api.client

import com.movie.binged.api.model.movies_collection.Movie
import com.movie.binged.api.model.movies_collection.MovieCollection
import com.movie.binged.api.model.movies_collection.MovieItem
import com.movie.binged.api.model.searchResult.SearchResultItem
import com.movie.binged.api.model.seasons_episodes.ShowSeasonData
import com.movie.binged.api.model.show_collection.Show
import com.movie.binged.api.model.show_collection.ShowCollection
import com.movie.binged.api.model.show_collection.ShowItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {
    @GET("movies/trending")
    suspend fun getTrendingMovies(): MovieCollection

    @GET("shows/trending")
    suspend fun getTrendingShows(): ShowCollection

    @GET("movies/popular")
    suspend fun getPopularMovies(): MovieItem

    @GET("shows/popular")
    suspend fun getPopularShows(): ShowItem

    @GET("movies/{id}?extended=full")
    suspend fun getMovieData( @Path("id") id : String ): Movie

    @GET("shows/{id}?extended=full")
    suspend fun getShowData( @Path("id") id : String ) : Show

    @GET("shows/{id}/seasons")
    suspend fun getSeasonData( @Path("id") id :String ) : List<ShowSeasonData>

    @GET("search")
    suspend fun getSearch(
        @Query("query") query: String,
        @Query("type") type: String = "show,movie"
    ): Response<List<SearchResultItem>>


}
