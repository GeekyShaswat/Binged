package com.movie.binged.utils

object Utils {

    @JvmStatic
    fun urlForMovie(tmdbId: String?) : String{
        return "https://www.vidking.net/embed/movie/$tmdbId?color=00696F"
    }

    @JvmStatic
    fun urlForSeries(tmdbId: String?, season: Int?, episode: Int?) :String {
        return "https://www.vidking.net/embed/tv/$tmdbId/$season/$episode?color=00696F&autoPlay=true&nextEpisode=true&episodeSelector=true"
    }
}