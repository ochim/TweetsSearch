package com.example.tweetssearch.repository

import com.example.tweetssearch.BuildConfig
import com.example.tweetssearch.model.Token
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import timber.log.Timber

private const val APP_TOKEN = BuildConfig.APP_TOKEN

interface AccessTokenInterface {
    @Headers("Content-Type:application/x-www-form-urlencoded;charset=UTF-8")
    @FormUrlEncoded
    @POST("oauth2/token")
    fun postAccessToken(
        @Header("Authorization") authorization: String = "Basic $APP_TOKEN",
        @Field("grant_type") type: String = "client_credentials"
    ): Call<AccessToken>
}

data class AccessToken(val token_type: String, val access_token: String)

class AccessTokenRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun getAccessToken(): String? {
        return withContext(ioDispatcher) {
            // Preferenceから取得する
            if (!Token.accessToken.isNullOrEmpty()) {
                return@withContext Token.accessToken
            }

            try {
                //WEB APIから取得する
                val accessTokenInterface =
                    TwitterRepository.retrofit.create(AccessTokenInterface::class.java)
                val response = accessTokenInterface.postAccessToken().execute()
                if (response.isSuccessful) {
                    val token = response.body()!!
                    Token.accessToken = token.access_token
                    token.access_token
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