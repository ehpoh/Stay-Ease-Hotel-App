package com.example.stayeasehotel.data.LostItemData

import android.net.Uri
import androidx.room.TypeConverter

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


import com.google.common.reflect.TypeToken
import com.google.gson.Gson


class Converters {

    private val gson = Gson()
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromUriList(uris: List<Uri>?): String? {
        return uris?.joinToString(",") { it.toString() }
    }

    @TypeConverter
    fun toUriList(data: String?): List<Uri>? {
        return data?.split(",")?.map { Uri.parse(it) }
    }

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    // LocalDate converters
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.format(dateFormatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, dateFormatter) }
    }

    // LocalTime converters
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, timeFormatter) }
    }

    @TypeConverter
    fun fromReporterInfo(value: ReporterInfo?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toReporterInfo(value: String?): ReporterInfo? {
        val type = object : TypeToken<ReporterInfo?>() {}.type
        return gson.fromJson(value, type)
    }


}
