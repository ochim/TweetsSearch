package com.example.tweetssearch.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tweetssearch.data.database.AppDatabase
import com.example.tweetssearch.data.database.KeywordHistory
import com.example.tweetssearch.data.database.KeywordHistoryDao
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Config(manifest= Config.NONE)
class KeywordsRepositoryTest {
    private lateinit var keywordsRepository: KeywordsRepository
    private lateinit var db: AppDatabase
    private lateinit var dao: KeywordHistoryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        dao = db.keywordHistoryDao()
        keywordsRepository = KeywordsRepository(Dispatchers.IO, dao)
        runBlocking {
            val time = System.currentTimeMillis()
            val history0 = KeywordHistory(0, "hoge", (time - 2000L) / 1000)
            val history1 = KeywordHistory(0, "foo", (time - 1000L) / 1000)
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

    @Test
    fun getRecentKeywords_success() {
        runBlocking {
            val keywords = keywordsRepository.getRecentKeywords()
            assertThat(keywords).isEqualTo(listOf("foo", "hoge"))
        }
    }

    @Test
    fun saveKeyword_success() {
        runBlocking {
            keywordsRepository.saveKeyword("piyo")
        }
        runBlocking {
            launch(Dispatchers.IO) {
                val keywords = dao.loadKeywords()
                assertThat(keywords).isEqualTo(listOf("piyo", "foo", "hoge"))
            }
        }
    }
}