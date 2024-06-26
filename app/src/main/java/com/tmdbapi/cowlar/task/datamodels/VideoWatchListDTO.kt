package com.tmdbapi.cowlar.task.datamodels

import com.google.gson.annotations.SerializedName

data class VideoWatchListDTO(
    @SerializedName("results")
    val results: List<VideoDTO>
) {
    data class VideoDTO(
        @SerializedName("key")
        val key: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("published_at")
        val publishedAt: String,
        @SerializedName("site")
        val site: String,
        @SerializedName("type")
        val type: String
    )
}

