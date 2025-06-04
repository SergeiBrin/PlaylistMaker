package com.example.playlistmaker.utils

import android.util.Log
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun extractYear(releaseDate: String): String {
    var year = ""

    try {
        val zonedDateTime = ZonedDateTime.parse(releaseDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        year = zonedDateTime.year.toString()
    } catch (e: DateTimeParseException) {
        Log.w("Info","Не удалось преобразовать строку releaseDate в ZonedDateTime")
    }

    return year
}