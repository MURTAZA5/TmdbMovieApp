package com.tmdbapi.cowlar.task.datamodels

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.codecollapse.tmdbmovies.models.datasources.utils.converters.GenresTypeConverter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MediaType
import org.jetbrains.annotations.NotNull


data class MovieSearchResult(
    @Json(name = "page")
    var page: Int? = 0,
    @Json(name = "results")
    var results: List<MovieDetails> = arrayListOf(),
    @Json(name = "total_pages")
    var total_pages: Int? = 0,
    @Json(name = "total_results")
    var total_results: Int? = 0
) {

    @Entity(tableName = "search_movie")
    data class MovieDetails(
        @Json(name = "adult")
        var adult: Boolean = false,
        @Json(name = "backdrop_path")
        var backdrop_path: String? = "",

        @Json(name = "genre_ids")
        @TypeConverters(GenresTypeConverter::class)
        var genre_ids: List<Int> = arrayListOf(),
        @NotNull
        @PrimaryKey
        @Json(name = "id")
        var id: Int = 0,
        @Json(name = "original_language")
        var original_language: String? = "",
        @Json(name = "original_title")
        var original_title: String? = "",
        @Json(name = "overview")
        var overview: String? = "",
        @Json(name = "popularity")
        var popularity: Float? = 0.0f,
        @Json(name = "poster_path")
        var poster_path: String? = "",
        @Json(name = "release_date")
        var release_date: String? = "",
        @Json(name = "title")
        var title: String? = "",
        @Json(name = "video")
        var video: Boolean = false,
        @Json(name = "vote_average")
        var vote_average: Float? = 0.0f,
        @Json(name = "vote_count")
        var vote_count: Int? = 0
    )
}



/*
@Keep
data class MovieSearchResult(
    @Json(name = "page")
    var page: Int? = 0,
    @Json(name = "results")
    var results: List<MovieDetails> = emptyList(),
    @Json(name = "total_pages")
    var totalPages: Int? = 0,
    @Json(name = "total_results")
    var totalResults: Int? = 0
) {
    @Keep
    @Entity(tableName = "search_movie")
    data class MovieDetails(
        @Json(name = "backdrop_path")
        var backdropPath: String? = "",
        @PrimaryKey
        @Json(name = "id")
        var id: Int? = 0,
        @Json(name = "original_name")
        var origional_name: String? = "",
        @Json(name = "overview")
        var overview: String? = "",
        @Json(name = "poster_path")
        var posterPath: String? = "",
        @Json(name = "media_type")
        var mediaType: String?="",
        @Json(name = "adult")
        var adult: Boolean? = false,
        @Json(name = "name")
        var name: String? = "",
        @Json(name = "original_language")
        var originalLanguage: String? = "",
        @Json(name = "genre_ids")
        var genreIds: List<Int> = emptyList(),
        @Json(name = "popularity")
        var popularity: Float? = 0.0f,
        @Json(name = "first_air_date")
        var releaseDate: String? = "",
        @Json(name = "vote_average")
        var voteAverage: Float = 0.0f,
        @Json(name = "vote_count")
        var voteCount: Int? = 0,
        @Json(name = "origin_country")
        var origionalCountry: List<Int> = emptyList(),
    )
}*/
