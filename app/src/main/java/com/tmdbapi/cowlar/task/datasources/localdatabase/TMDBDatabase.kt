package com.tmdbapi.cowlar.task.datasources.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tmdbapi.cowlar.task.utility.AppConstants
import com.codecollapse.tmdbmovies.models.datamodels.MovieCredits
import com.codecollapse.tmdbmovies.models.datamodels.MovieDetail
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDao
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDetailDao
import com.codecollapse.tmdbmovies.models.datasources.utils.converters.GenresTypeConverter
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import com.tmdbapi.cowlar.task.datasources.localdatabase.converters.GenreConverter
import com.tmdbapi.cowlar.task.datasources.localdatabase.dao.MovieCastDao

@Database(
    entities = [TMDBMovies.Results::class, MovieDetail::class,
        MovieCredits.MovieCast::class, MovieSearchResult.MovieDetails::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    GenresTypeConverter::class, GenreConverter::class
)
abstract class TMDBDatabase : RoomDatabase() {

    abstract fun moviesDao(): MovieDao
    abstract fun movieDetailDao(): MovieDetailDao
    abstract fun movieCastDao(): MovieCastDao

    companion object {
        val DATABASE_NAME = AppConstants.APP_DATABASE
    }
}