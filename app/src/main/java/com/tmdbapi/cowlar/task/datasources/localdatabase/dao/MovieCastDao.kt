package com.tmdbapi.cowlar.task.datasources.localdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codecollapse.tmdbmovies.models.datamodels.MovieCredits
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult

@Dao
interface MovieCastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieCast(movieCast : MovieCredits.MovieCast)

    @Query("SELECT *FROM movie_cast where movieId=:movieId order by cast_id asc limit 8")
    fun getMovieCast(movieId : Int) : List<MovieCredits.MovieCast>

     @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieSearch(movieCast : MovieSearchResult.MovieDetails)

//    @Query("SELECT *FROM search_movie where id=:movieId order by releaseDate")
//    fun getSearchResult(movieId : Int) : List<MovieSearchResult.MovieDetails>
//
    @Query("SELECT *FROM search_movie")
    fun getSearchResult() : List<MovieSearchResult.MovieDetails>


}