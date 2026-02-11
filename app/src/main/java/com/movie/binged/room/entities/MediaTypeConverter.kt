package com.movie.binged.room.entities

import androidx.room.TypeConverter

class MediaTypeConverter {

    @TypeConverter
    fun fromMediaType(type: MediaType): String = type.name

    @TypeConverter
    fun toMediaType(value: String): MediaType = MediaType.valueOf(value)
}
