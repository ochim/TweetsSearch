package com.example.tweetssearch.data.repository

import androidx.annotation.Keep
import com.example.tweetssearch.model.Tweet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

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

@Keep
data class SearchResult(val statuses: List<Tweet>?)

class TweetsRemoteDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val searchInterface: TweetsSearchInterface
) {

    suspend fun tweetsSearch(
        q: String,
        count: Int,
        max_id: Long?,
        accessTokenRepository: AccessTokenRepository
    ): List<Tweet>? {
        return withContext(ioDispatcher) {
            val accessToken = accessTokenRepository.getAccessToken()
            if (accessToken.isNullOrEmpty()) {
                throw Exception("accessToken error")
            }

            if (max_id == null) {
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
                        accessTokenRepository.clear()
                    }
                    throw Exception("TweetsSearch error code ${response.code()} ${response.message()}")
                }

            } else {
                //WEB APIから取得する
                val response = searchInterface
                        // 次候補なのでmax_id -1
                    .nextTweetsSearch("Bearer $accessToken", q, max_id -1, count)
                    .execute()
                if (response.isSuccessful) {
                    val result = response.body()!!
                    result.statuses
                } else {
                    if (response.code() == 401) {
                        // アクセストークンが無効なので消す
                        accessTokenRepository.clear()
                    }
                    throw Exception("NextTweetsSearch error code ${response.code()} ${response.message()}")
                }

            }

        }
    }

}