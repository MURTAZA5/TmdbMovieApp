package com.tmdbapi.cowlar.task.datasources.api

import com.codecollapse.tmdbmovies.models.datamodels.MovieCredits
import com.codecollapse.tmdbmovies.models.datamodels.MovieDetail
import com.tmdbapi.cowlar.task.datamodels.MovieSearchResult
import com.tmdbapi.cowlar.task.datamodels.TMDBMovies
import com.tmdbapi.cowlar.task.datamodels.VideoWatchListDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {
    @GET("trending/all/day")
    suspend fun getTrendingMovies(@Query("api_key") apiKey: String): Response<TMDBMovies>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String): Response<TMDBMovies>

    @GET("movie/upcoming")
    suspend fun getUpComingMovies(@Query("api_key") apiKey: String): Response<TMDBMovies>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("append_to_response") videos: String
    ): Response<MovieDetail>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Response<MovieCredits>

    /*    @GET("search/movie")
        suspend fun getBySearch(
            @Query("query") query: String,
            @Query("api_key") apiKey: String
        ): Response<MovieSearchResult>*/


    @GET("search/movie")
    suspend fun getBySearch(
        @Query("query") query: String,
        @Query("api_key") apiKey: String
    ): Response<MovieSearchResult>


    @GET("search/multi")
    suspend fun getBySearchMulti(
        @Query("api_key")
        api_key: String ,
        @Query("language")
        language: String = "en-US",
        @Query("query")
        query: String,
        @Query("page")
        page: Int = 1,
        @Query("include_adult")
        include_adult: Boolean = false
    ): Response<MovieSearchResult>

    @GET("movie/{movieId}/videos")
    suspend fun getMovieTrailers(
        @Path("movieId") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<VideoWatchListDTO>
}