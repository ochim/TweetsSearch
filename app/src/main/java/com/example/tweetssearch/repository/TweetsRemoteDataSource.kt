package com.example.tweetssearch.repository

import com.example.tweetssearch.model.Token
import com.example.tweetssearch.model.Tweet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.lang.Exception

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

class TweetsRemoteDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val searchInterface: TweetsSearchInterface =
        TwitterRepository.retrofit.create(TweetsSearchInterface::class.java)
) {

    suspend fun tweetsSearch(
        accessToken: String,
        q: String,
        count: Int,
        max_id: Long?,
    ): List<Tweet>? {
        return withContext(ioDispatcher) {
            if (max_id == null) {
                //WEB APIから取得する
                val response = searchInterface
                    .tweetsSearch("Bearer $accessToken", q, count)
                    .execute()
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if (result.statuses.isNullOrEmpty()) {
                        throw Exception("empty")
                    }
                    result.statuses

                } else {
                    if (response.code() == 401) {
                        // アクセストークンが無効なので消す
                        Token.accessToken = null
                    }
                    throw Exception("error code ${response.code()}")
                }

            } else {
                //WEB APIから取得する
                val response = searchInterface
                    .nextTweetsSearch("Bearer $accessToken", q, max_id, count)
                    .execute()
                if (response.isSuccessful) {
                    val result = response.body()!!
                    result.statuses
                } else {
                    if (response.code() == 401) {
                        // アクセストークンが無効なので消す
                        Token.accessToken = null
                    }
                    throw Exception("error code ${response.code()}")
                }

            }

        }
    }

}