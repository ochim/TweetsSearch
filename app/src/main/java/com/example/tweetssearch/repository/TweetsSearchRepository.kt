package com.example.tweetssearch.repository

import com.example.tweetssearch.model.Token
import com.example.tweetssearch.model.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import timber.log.Timber

interface TweetsSearchInterface {
    @GET("1.1/search/tweets.json")
    fun tweetsSearch(
        @Header("Authorization") authorization: String,
        @Query("q") q: String,
        @Query("count") count: Int,
    ): Call<SearchResult>

    @GET("1.1/search/tweets.json")
    fun nextTweetsSearch(
        @Header("Authorization") authorization: String,
        @Query("q") q: String,
        @Query("max_id") id: Long,
        @Query("count") count: Int,
    ): Call<SearchResult>

}

data class SearchResult(val statuses: List<Tweet>?)

class TweetsSearchRepository {

    private val searchInterface: TweetsSearchInterface =
        TwitterRepository.retrofit.create(TweetsSearchInterface::class.java)

    suspend fun tweetsSearch(accessToken: String, q: String, count: Int): List<Tweet>? {
        return withContext(Dispatchers.IO) {
            try {
                //WEB APIから取得する
                val response = searchInterface
                    .tweetsSearch("Bearer $accessToken", q, count)
                    .execute()
                if (response.isSuccessful) {
                    val result = response.body()!!
                    result.statuses
                } else {
                    if (response.code() == 401) {
                        // アクセストークンが無効なので消す
                        Token.accessToken = null
                    }
                    null
                }

            } catch (ex: Exception) {
                Timber.e(ex.toString())
                null
            }

        }
    }

    suspend fun nextTweetsSearch(
        accessToken: String,
        q: String,
        max_id: Long,
        count: Int
    ): List<Tweet>? {
        return withContext(Dispatchers.IO) {
            try {
                //WEB APIから取得する
                val response = searchInterface
                    .nextTweetsSearch("Bearer $accessToken", q, max_id, count)
                    .execute()
                if (response.isSuccessful) {
                    val result = response.body()!!
                    result.statuses
                } else {
                    null
                }

            } catch (ex: Exception) {
                Timber.e(ex.toString())
                null
            }
        }

    }
}