package com.example.tweetssearch.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tweetssearch.dataStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate

@RunWith(AndroidJUnit4::class)
@Config(manifest=Config.NONE)
class AccessTokenRepositoryTest {
    private lateinit var repository: AccessTokenRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = AccessTokenRepository(
            accessTokenInterface = MockApi.createAccessTokenService(),
            context = context
        )
        runBlocking {
            context.dataStore.edit { it.clear() }
        }
    }

    @Test
    fun accessToken_first_null() {
        runBlocking {
            val token = repository.accessTokenInDataStore()
            assertThat(token).isNull()
        }
    }

    @Test
    fun getAccessToken_from_service() {
        runBlocking {
            val token = repository.getAccessToken()
            assertThat(token).isEqualTo("access_token")
        }
    }

    @Test
    fun getAccessToken_save_accessToken() {
        runBlocking {
            repository.getAccessToken()
            val token = repository.accessTokenInDataStore()
            assertThat(token).isEqualTo("access_token")
        }
    }

    @Test
    fun clear_accessToken() {
        runBlocking {
            repository.getAccessToken()
            repository.clear()
            val token = repository.accessTokenInDataStore()
            assertThat(token).isEqualTo("")
        }
    }
}

class MockAccessTokenService(private val delegate: BehaviorDelegate<AccessTokenInterface>) :
    AccessTokenInterface {

    override fun postAccessToken(authorization: String, type: String): Call<AccessToken> {
        return delegate
            .returningResponse(AccessToken("token_type", "access_token"))
            .postAccessToken(authorization, type)
    }
}