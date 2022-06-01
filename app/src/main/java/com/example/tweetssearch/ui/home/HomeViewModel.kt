package com.example.tweetssearch.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tweetssearch.data.repository.AccessTokenRepository
import com.example.tweetssearch.data.repository.KeywordsRepository
import com.example.tweetssearch.data.repository.TweetsSearchRepository
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.TweetNetworkModelState
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor (
    private val tweetsSearchRepository: TweetsSearchRepository,
    private val keywordsRepository: KeywordsRepository,
    private val accessTokenRepository: AccessTokenRepository
) : ViewModel() {

    private val mLiveState: MutableLiveData<TweetNetworkModelState<List<Tweet>>> =
        MutableLiveData(TweetNetworkModelState.NeverFetched)
    val liveState: LiveData<TweetNetworkModelState<List<Tweet>>> get() = mLiveState

    var keywordsState by mutableStateOf<List<String>>(emptyList())
        private set

    var requireInputState by mutableStateOf(true)

    private var nowTweets: List<Tweet>? = null
    private var nowQuery: String? = null

    fun tweetsSearch(q: String) {
        // 検索中ならすぐ返す
        if (mLiveState.value == TweetNetworkModelState.Fetching) return

        mLiveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {
            try {
                val list =
                    tweetsSearchRepository.tweetsSearch(
                        q,
                        FIRST_PAGE_SIZE,
                        null,
                        accessTokenRepository
                    )
                if (!list.isNullOrEmpty()) {
                    mLiveState.postValue(TweetNetworkModelState.FetchedOK(list))
                    nowTweets = list
                } else {
                    mLiveState.postValue(TweetNetworkModelState.FetchedError(Exception("empty")))
                }

            } catch (e: Exception) {
                mLiveState.postValue(TweetNetworkModelState.FetchedError(e))
            }

            nowQuery = q
            // キーワード履歴に入れる
            keywordsRepository.saveKeyword(q)
        }
    }

    fun nextTweetsSearch() {
        val q = nowQuery ?: return
        val tweet = nowTweets?.last() ?: return // 現行の最古のツイート

        if (mLiveState.value == TweetNetworkModelState.Fetching) return
        mLiveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {
            val list: List<Tweet>?

            try {
                list =
                    tweetsSearchRepository.tweetsSearch(
                        q,
                        FIRST_PAGE_SIZE,
                        tweet.id,
                        accessTokenRepository
                    )

            } catch (e: Exception) {
                mLiveState.postValue(TweetNetworkModelState.FetchedError(e))
                return@launch
            }

            if (list.isNullOrEmpty()) {
                mLiveState.postValue(TweetNetworkModelState.FetchedOK(emptyList()))
                return@launch
            }

            val newList = nowTweets!!.toMutableList()

            // 現行の最古のツイートと次候補の先頭のツイートが同じ場合、現行の最古のツイートを消す。
            if (list.first().id == tweet.id) {
                newList.remove(tweet)
            }
            newList.addAll(list)

            nowTweets = newList.toList()
            mLiveState.postValue(TweetNetworkModelState.FetchedOK(newList))
        }
    }

    fun loadKeywordsHistory() {
        viewModelScope.launch {
            val keywords = keywordsRepository.getRecentKeywords()
            if (!keywords.isNullOrEmpty()) keywordsState = keywords
        }
    }

    companion object {
        const val FIRST_PAGE_SIZE = 15
    }

}

class HomeViewModelFactory(
    private val tweetsSearchRepository: TweetsSearchRepository,
    private val keywordsRepository: KeywordsRepository,
    private val accessTokenRepository: AccessTokenRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(tweetsSearchRepository, keywordsRepository, accessTokenRepository) as T
    }
}