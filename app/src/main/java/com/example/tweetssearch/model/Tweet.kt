package com.example.tweetssearch.model

import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.Locale

data class Tweet(
    val text: String,
    @Json(name = "created_at") val createdAt: String,
    val id: Long,
    val user: User,
)
