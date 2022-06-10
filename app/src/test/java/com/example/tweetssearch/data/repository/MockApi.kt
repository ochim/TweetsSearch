package com.example.tweetssearch.data.repository

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

class MockApi {

    companion object {

        fun createAccessTokenService(): MockAccessTokenService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://mock.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            val behavior = NetworkBehavior.create()

            val mockRetrofit = MockRetrofit.Builder(retrofit)
                .networkBehavior(behavior)
                .build()

            val delegate = mockRetrofit.create(AccessTokenInterface::class.java)
            return MockAccessTokenService(delegate)
        }
    }
}