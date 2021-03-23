package com.example.tweetssearch.model

data class Tweet(
    val text: String,
    val createdAt: String
)

val dummyTweets = listOf(
    Tweet("hogehoge", "2021/03/22 14:00:00"),
    Tweet("foofoo", "2021/03/22 13:00:00"),
    Tweet("piyopiyo", "2021/03/22 12:00:00")
)
