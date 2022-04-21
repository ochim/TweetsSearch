package com.example.tweetssearch.repository

import com.example.tweetssearch.model.Tweet

class TweetsSearchRepository(
    private val tweetsRemoteDataSource: TweetsRemoteDataSource
    ) {

    suspend fun tweetsSearch(
        q: String,
        count: Int,
        max_id: Long?,
        accessTokenRepository: AccessTokenRepository
    ) : List<Tweet>? {
        return tweetsRemoteDataSource.tweetsSearch(q, count, max_id, accessTokenRepository)
    }
}