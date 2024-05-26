package com.codecollapse.tmdbmovies.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.codecollapse.tmdbmovies.models.datamodels.MovieCredits
import com.codecollapse.tmdbmovies.models.datasources.utils.Resource
import com.codecollapse.tmdbmovies.models.repositories.StartUpRepository
import com.tmdbapi.cowlar.task.datamodels.Media
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(private var startUpRepository: StartUpRepository) :
    ViewModel() {

    fun getMovieDetails(movieId: Int, movieLanguage: String) =
        startUpRepository.getMovieDetails(movieId, movieLanguage)

    fun getMovieTrailerDetails(movieId: Int) =
        startUpRepository.getMovieTrailor(movieId)

    fun getUpComingMovies(): Flow<Resource<List<TMDBMovies.Results>>> =
        startUpRepository.getUpComingMovies()

    fun getMovieCredits(
        movieId: Int,
        movieLanguage: String
    ): Flow<Resource<List<MovieCredits.MovieCast>>> =
        startUpRepository.getMovieCredits(movieId, movieLanguage)

    fun getSearchResult(query: String): Flow<Resource<List<MovieSearchResult.MovieDetails>>> =
        startUpRepository.getMovieSearch(query)

    fun getMovieSearchingResult(query: String): Flow<Resource<List<MovieSearchResult.MovieDetails>>> =
        startUpRepository.getMovieSearchingResult(query)

    fun getSearchCacheData(): Flow<Resource<List<MovieSearchResult.MovieDetails>>> =
        startUpRepository.getSearchCacheData()


}