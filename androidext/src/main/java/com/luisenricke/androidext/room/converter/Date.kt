package com.luisenricke.androidext.room.converter

import androidx.room.TypeConverter
import java.util.Date

@Suppress("unused")
object Date {
    @TypeConverter
    @JvmStatic
    fun fromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
