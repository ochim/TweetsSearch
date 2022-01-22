package com.example.tweetssearch.model

/**
 * Tweet読み込みの通信状況
 */
sealed class TweetNetworkModelState {
    /** まだ読み込みをしていない */
    object NeverFetched : TweetNetworkModelState()

    /** 読み込み中 */
    object Fetching : TweetNetworkModelState()

    /** 読み込みが終わって正常系 */
    class FetchedOK(val list: List<Tweet>) : TweetNetworkModelState()

    /** 読み込みが終わってエラー系 */
    class FetchedError(val exception: Exception) : TweetNetworkModelState()
}