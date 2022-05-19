package com.example.tweetssearch.util

import java.text.SimpleDateFormat
import java.util.Locale

class TweetUtil {

    fun convert(createdAtText: String) : String {
        try {
            val format = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
            val date = format.parse(createdAtText)
            date ?: return "null error"

            val newFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US)
            return newFormat.format(date)
        } catch (e: Exception) {
            return e::class.java.name
        }

    }
}