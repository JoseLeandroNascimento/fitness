package com.example.fitness.models

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {

    @TypeConverter
    fun toDate(dateLong: Long?):Date?{

        return if (dateLong != null) Date(dateLong) else null

    }

    @TypeConverter
    fun fromDate(date:Date?):Long?{

        return date?.time
    }

}