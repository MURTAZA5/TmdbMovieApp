package com.codecollapse.tmdbmovies.models.repositories

import android.util.Log
import com.codecollapse.tmdbmovies.models.datamodels.MovieCredits
import com.codecollapse.tmdbmovies.models.datamodels.MovieDetail
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDao
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDetailDao
import com.codecollapse.tmdbmovies.models.datasources.utils.Resource
import com.tmdbapi.cowlar.task.BuildConfig
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import com.tmdbapi.cowlar.task.datamodels.VideoWatchListDTO
import com.tmdbapi.cowlar.task.datasources.api.TMDBApi
import com.tmdbapi.cowlar.task.datasources.localdatabase.dao.MovieCastDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StartUpRepository @Inject constructor(
    private var movieDao: MovieDao,
    private var tmdbApi: TMDBApi,
    private var movieDetailDao: MovieDetailDao,
    private var movieCastDao: MovieCastDao
) {
    companion object {
        private const val TAG = "StartUpRepository"
    }

    fun getUpComingMovies(): Flow<Resource<List<TMDBMovies.Results>>> {
        return flow {
            try {
                emit(Resource.success(movieDao.getTMDBMovies("upcoming")))
                tmdbApi.getUpComingMovies(BuildConfig.SECRET_KEY).let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "getTrendingMoviesList: ${it.body()!!.results}")
                        it.body()!!.results.forEach { movie->
                            movie.movieType = "upcoming"
                            movieDao.insertMovies(movie)
                        }
                        emit(Resource.success(movieDao.getTMDBMovies("upcoming")))
                    } else {
                        emit(Resource.error(it.body()!!.status_message!!, data = null))
                    }
                }
            }
            catch (ex : Exception){
                Log.e(TAG, "getMovieDetails: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }


    fun getMovieDetails(movieId: Int, movieLanguage: String): Flow<Resource<MovieDetail>> {
        return flow {
            try {
                emit(Resource.success(movieDetailDao.getTMDBMovieById(movieId)))
                tmdbApi.getMovieDetails(movieId,BuildConfig.SECRET_KEY, movieLanguage,"videos").let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "getMovieDetails: ${it.body()!!}")
                        movieDetailDao.insertMovie(it.body()!!)
                        emit(Resource.success(movieDetailDao.getTMDBMovieById(movieId)))
                    } else {
                        emit(Resource.error("something went wrong", data = null))
                    }
                }
            }catch (ex : Exception){
                Log.e(TAG, "getMovieDetails: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }
   fun getMovieTrailor(movieId: Int): Flow<Resource<List<VideoWatchListDTO.VideoDTO>>>{
        return flow {
            try {
                tmdbApi.getMovieTrailers(movieId,BuildConfig.SECRET_KEY).let {
                    if (it!=null && it.isSuccessful) {
                        emit(Resource.success(it.body()!!.results))
                    } else {
                        emit(Resource.error("something went wrong", data = null))
                    }
                }
            }catch (ex : Exception){
                Log.e(TAG, "getMovieDetails: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }


    fun getSearchCacheData(): Flow<Resource<List<MovieSearchResult.MovieDetails>>> {
        return flow {
            try {
                emit(Resource.success(movieCastDao.getSearchResult()))
            }catch (ex : Exception){
                Log.e(TAG, "getMovieDetails: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }
    fun getMovieSearch(query: String): Flow<Resource<List<MovieSearchResult.MovieDetails>>> {
        return flow {
            try {
                tmdbApi.getBySearch( query,BuildConfig.SECRET_KEY).let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "getMovieSearch: ${it.body()!!}")
                        it.body()!!.results.forEach { movieCast ->
                            movieCastDao.insertMovieSearch(movieCast)
                        }
                        emit(Resource.success(movieCastDao.getSearchResult()))
                    } else {
                        emit(Resource.error("something went wrong", data = null))
                    }
                }
            }catch (ex : Exception){
                Log.e(TAG, "getMovieSearch: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }

    fun getMovieSearchingResult(query: String): Flow<Resource<List<MovieSearchResult.MovieDetails>>> {
        return flow {
            try {
                tmdbApi.getBySearchMulti( query = query, api_key = BuildConfig.SECRET_KEY).let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "getMovieSearch: ${it.body()!!}")
//                        it.body()!!.results.forEach { movieCast ->
//                        }
                        emit(Resource.success(it.body()!!.results))
                    } else {
                        emit(Resource.error("something went wrong", data = null))
                    }
                }
            }catch (ex : Exception){
                Log.e(TAG, "getMovieSearch: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }



    fun getMovieCredits(movieId: Int, movieLanguage: String): Flow<Resource<List<MovieCredits.MovieCast>>> {
        return flow {
            try {
                emit(Resource.success(movieCastDao.getMovieCast(movieId)))
                tmdbApi.getMovieCredits(movieId,BuildConfig.SECRET_KEY, movieLanguage).let {
                    if (it.isSuccessful) {
                        Log.e(TAG, "getMovieDetails: ${it.body()!!}")
                        it.body()!!.cast.forEach { movieCast ->
                            movieCast.movieId = movieId
                            movieCastDao.insertMovieCast(movieCast)
                        }
                        emit(Resource.success(movieCastDao.getMovieCast(movieId)))
                    } else {
                        emit(Resource.error("something went wrong", data = null))
                    }
                }
            }catch (ex : Exception){
                Log.e(TAG, "getMovieDetails: ${ex.message}")
                emit(Resource.error("something went wrong", data = null))
            }

        }.flowOn(IO)
    }
}