package com.tmdbapi.cowlar.task.datasources.localdatabase.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenreConverter {
    @TypeConverter
    fun fromGenreIdsList(genreIds: List<Int>?): String? {
        return genreIds?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toGenreIdsList(genreIdsString: String?): List<Int>? {
        return genreIdsString?.let {
            val listType = object : TypeToken<List<Int>>() {}.type
            Gson().fromJson(it, listType)
        }
    }
}