package com.example.tweetssearch.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tweetssearch.BuildConfig
import com.example.tweetssearch.dataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

private const val APP_TOKEN = BuildConfig.APP_TOKEN
private val ACCESS_TOKEN = stringPreferencesKey("access_token")

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

class AccessTokenRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val accessTokenInterface: AccessTokenInterface,
    private val context: Context
) {

    suspend fun getAccessToken(): String? {
        return withContext(ioDispatcher) {

            val accessTokenData = runBlocking { accessTokenInDataStore() }
            if (!accessTokenData.isNullOrEmpty()) {
                return@withContext accessTokenData
            }

            //WEB APIから取得する
            val response = accessTokenInterface.postAccessToken().execute()
            if (response.isSuccessful) {
                val accessToken = response.body()!!
                context.dataStore.edit { settings ->
                    settings[ACCESS_TOKEN] = accessToken.access_token
                }
                accessToken.access_token
            } else {
                throw Exception("AccessToken error code ${response.code()} ${response.message()}")
            }

        }
    }

    suspend fun clear() {
        context.dataStore.edit { settings ->
            settings[ACCESS_TOKEN] = ""
        }
    }

    suspend fun accessTokenInDataStore(): String? {
        val accessTokenData = context.dataStore.data.first()
        return accessTokenData[ACCESS_TOKEN]
    }
}