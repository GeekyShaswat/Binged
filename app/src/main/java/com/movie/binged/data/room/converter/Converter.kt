package com.movie.binged.data.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.movie.binged.api.model.movies_collection.Ids
import com.movie.binged.api.model.movies_collection.Images

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromIds(ids: Ids?): String? {
        return gson.toJson(ids)
    }

    @TypeConverter
    fun toIds(idsString: String?): Ids? {
        return gson.fromJson(idsString, Ids::class.java)
    }

    @TypeConverter
    fun fromImages(images: Images?): String? {
        return gson.toJson(images)
    }

    @TypeConverter
    fun toImages(imagesString: String?): Images? {
        return gson.fromJson(imagesString, Images::class.java)
    }
}