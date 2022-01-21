package com.example.tweetssearch.repository

import com.example.tweetssearch.model.Tweet

class TweetsSearchRepository(
    private val tweetsRemoteDataSource: TweetsRemoteDataSource
    ) {

    suspend fun tweetsSearch(
        accessToken: String,
        q: String,
        count: Int,
        max_id: Long?,
        ) : List<Tweet>? {
        return tweetsRemoteDataSource.tweetsSearch(accessToken, q, count, max_id)
    }
}