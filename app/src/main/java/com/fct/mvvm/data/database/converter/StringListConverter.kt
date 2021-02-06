package com.fct.mvvm.data.database.converter

import androidx.room.TypeConverter

private const val DELIMITER = "^"

/**
 * Converts a list of strings to a delimited string and then back
 */
class StringListConverter {
    @TypeConverter
    fun fromString(list: String) = list
        .takeIf { it.isNotEmpty() } // catch empty String list issue
        ?.split(DELIMITER)
        ?.map { it }

    @TypeConverter
    fun toString(list: List<String>) = list.joinToString(separator = DELIMITER)
}