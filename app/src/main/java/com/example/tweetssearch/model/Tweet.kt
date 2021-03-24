package com.example.tweetssearch.model

import com.squareup.moshi.Json

data class Tweet(
    val text: String,
    @Json(name = "created_at") val createdAt: String,
    val user: User
)