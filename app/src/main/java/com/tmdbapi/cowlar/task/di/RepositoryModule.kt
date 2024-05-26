package com.tmdbapi.cowlar.task.di

import android.content.Context
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDao
import com.codecollapse.tmdbmovies.models.datasources.local.dao.MovieDetailDao
import com.codecollapse.tmdbmovies.models.repositories.StartUpRepository
import com.tmdbapi.cowlar.task.datasources.api.TMDBApi
import com.tmdbapi.cowlar.task.datasources.localdatabase.dao.MovieCastDao
import com.tmdbapi.cowlar.task.utility.NetworkConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideStartUpRepository(
        movieDao: MovieDao,
        tmdbApi: TMDBApi,
        movieDetailDao: MovieDetailDao,
        movieCastDao: MovieCastDao
    ): StartUpRepository {
        return StartUpRepository(
            movieDao = movieDao,
            tmdbApi = tmdbApi,
            movieDetailDao = movieDetailDao,
            movieCastDao
        )
    }

}