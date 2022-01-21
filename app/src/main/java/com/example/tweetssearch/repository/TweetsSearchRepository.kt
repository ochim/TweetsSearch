package com.example.tweetssearch.repository

import androidx.lifecycle.MutableLiveData
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.TweetNetworkModelState

class TweetsSearchRepository(
    private val tweetsRemoteDataSource: TweetsRemoteDataSource
    ) {

    suspend fun tweetsSearch(
        accessToken: String,
        q: String,
        count: Int,
        state: MutableLiveData<TweetNetworkModelState>,
        max_id: Long?,
        ) : List<Tweet>? {
        return tweetsRemoteDataSource.tweetsSearch(accessToken, q, count, state, max_id)
    }
}