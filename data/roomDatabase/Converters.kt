package com.example.stayeasehotel.data.roomDatabase

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.Default.decodeFromString(value)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.Default.encodeToString(list)
    }
}