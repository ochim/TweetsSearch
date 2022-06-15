package com.example.tweetssearch.model

/**
 * Tweet読み込みの通信状況
 */
sealed class TweetNetworkModelState<out T> {
    /** まだ読み込みをしていない */
    object NeverFetched : TweetNetworkModelState<Nothing>()

    /** 読み込み中 */
    object Fetching : TweetNetworkModelState<Nothing>()

    /** 更新読み込みが終わって正常系 */
    class RefreshedOK<out T>(val data: T) : TweetNetworkModelState<T>()

    /** 追加読み込みが終わって正常系 */
    class AppendedOK<out T>(val data: T) : TweetNetworkModelState<T>()

    /** 読み込みが終わってエラー系 */
    class FetchedError(val exception: Exception) : TweetNetworkModelState<Nothing>()
}