package com.example.tweetssearch.data.repository

import com.example.tweetssearch.ui.home.MockTweetsSearchService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

class MockApi {

    companion object {

        fun createAccessTokenService(): MockAccessTokenService {
            val delegate = mockRetrofit().create(AccessTokenInterface::class.java)
            return MockAccessTokenService(delegate)
        }

        fun createMockTweetsSearchService(): MockTweetsSearchService {
            val delegate = mockRetrofit().create(TweetsSearchInterface::class.java)
            return MockTweetsSearchService(delegate)
        }

        private fun mockRetrofit(): MockRetrofit {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://mock.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            val behavior = NetworkBehavior.create()

            return MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build()
        }
    }
}