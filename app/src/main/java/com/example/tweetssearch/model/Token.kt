package com.example.tweetssearch.model

import com.chibatching.kotpref.KotprefModel

object Token : KotprefModel() {
    var accessToken by nullableStringPref()
}