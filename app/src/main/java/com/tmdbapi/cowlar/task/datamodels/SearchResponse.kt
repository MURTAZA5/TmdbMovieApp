package com.tmdbapi.cowlar.task.datamodels

import androidx.annotation.Keep
import androidx.room.Entity
import com.tmdbapi.cowlar.task.utility.Converter
import okhttp3.MediaType
import java.io.Serializable

@Keep
data class SearchResponse(
    val page: Int,
    val results: MutableList<Media>,
    val total_pages: Int,
    val total_results: Int
)


