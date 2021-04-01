package com.example.tweetssearch.util

import java.text.SimpleDateFormat
import java.util.Locale

class TweetUtil {

    fun convertCreatedAt(text: String) : String {
        try {
            val format = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
            val date = format.parse(text)
            val newFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US)
            date ?: return "format error"
            return newFormat.format(date)
        } catch (e: Exception) {
            return "format error"
        }

    }
}