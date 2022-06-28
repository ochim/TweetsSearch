package com.example.tweetssearch.ui.home

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tweetssearch.data.database.AppDatabase
import com.example.tweetssearch.data.database.KeywordHistory
import com.example.tweetssearch.data.repository.AccessTokenRepository
import com.example.tweetssearch.data.repository.KeywordsRepository
import com.example.tweetssearch.data.repository.MockApi
import com.example.tweetssearch.data.repository.SearchResult
import com.example.tweetssearch.data.repository.TweetsRemoteDataSource
import com.example.tweetssearch.data.repository.TweetsSearchInterface
import com.example.tweetssearch.data.repository.TweetsSearchRepository
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.User
import com.google.common.truth.Truth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var db: AppDatabase
    private lateinit var keywordsRepository: KeywordsRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        val dao = db.keywordHistoryDao()
        keywordsRepository = KeywordsRepository(Dispatchers.IO, dao)
        viewModel = HomeViewModel(
            TweetsSearchRepository(
                TweetsRemoteDataSource(searchInterface = MockApi.createMockTweetsSearchService())
            ),
            keywordsRepository,
            AccessTokenRepository(
                accessTokenInterface = MockApi.createAccessTokenService(),
                context = context
            )
        )
        runBlocking {
            val now = System.currentTimeMillis()
            val history0 = KeywordHistory(0, "hoge", (now - 2000L) / 1000)
            val history1 = KeywordHistory(0, "foo", (now - 1000L) / 1000)
            launch(Dispatchers.IO) {
                dao.insertAll(history0, history1)
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadKeywordsHistory_success() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        runTest {
            viewModel.loadKeywordsHistory()
        }
        Truth.assertThat(viewModel.keywordsState).isEqualTo(listOf("foo", "hoge"))
        Dispatchers.resetMain()
    }

}

class MockTweetsSearchService(private val delegate: BehaviorDelegate<TweetsSearchInterface>) :
    TweetsSearchInterface {

    override fun tweetsSearch(
        authorization: String, q: String, count: Int
    ): Call<SearchResult> {
        val tweet = Tweet("Hello", "Sun Jun 26 13:00:00 +0000 2022", 1, User("taro", "taro", null))
        return delegate
            .returningResponse(SearchResult(listOf(tweet)))
            .tweetsSearch(authorization, q, count)
    }

    override fun nextTweetsSearch(
        authorization: String, q: String, id: Long, count: Int
    ): Call<SearchResult> {
        val tweet = Tweet("Hello World", "Sun Jun 26 13:00:01 +0000 2022", 2, User("taro", "taro", null))
        return delegate
            .returningResponse(SearchResult(listOf(tweet)))
            .nextTweetsSearch(authorization, q, id, count)
    }

}