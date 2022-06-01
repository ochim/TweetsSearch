package com.example.tweetssearch.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val screen_name: String,
    val profile_image_url_https: String?
) : Parcelable