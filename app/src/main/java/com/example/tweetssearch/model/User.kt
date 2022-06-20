package com.example.tweetssearch.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class User(
    val name: String,
    val screen_name: String,
    val profile_image_url_https: String?
) : Parcelable