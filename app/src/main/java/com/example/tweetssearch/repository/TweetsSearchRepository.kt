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
        @Query("q") q: String,
        @Header("Authorization") authorization: String,
    ): Call<SearchResult>
}

data class SearchResult(val statuses: List<Tweet>?)

class TweetsSearchRepository {

    suspend fun tweetsSearch(accessToken: String, q: String): List<Tweet>? {
        return withContext(Dispatchers.IO) {
            try {
                //WEB APIから取得する
                val searchInterface =
                    TwitterRepository.retrofit.create(TweetsSearchInterface::class.java)
                val response = searchInterface.tweetsSearch(q, "Bearer $accessToken").execute()
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
}