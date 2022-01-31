package com.example.tweetssearch

import com.example.tweetssearch.model.Tweet

enum class FetchState {
    NEVER_FETCHED,
    FETCHING,
    FETCHED_OK,
    FETCHED_ERROR,
}

data class TweetsUiState(
    val fetchState: FetchState = FetchState.NEVER_FETCHED,
    val tweetsItems: List<Tweet> = listOf(),
    val userMessage: String? = null
)

data class KeywordsState(
    val keywordsItems: List<String> = listOf()
)
