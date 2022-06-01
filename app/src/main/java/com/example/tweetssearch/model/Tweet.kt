package com.example.tweetssearch.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tweet(
    val text: String,
    @Json(name = "created_at") val createdAt: String,
    val id: Long,
    val user: User,
) : Parcelable
