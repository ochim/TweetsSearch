package com.example.tweetssearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweetssearch.model.Token
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.TweetNetworkModelState
import com.example.tweetssearch.repository.AccessTokenRepository
import com.example.tweetssearch.repository.KeywordsRepository
import com.example.tweetssearch.repository.TweetsSearchRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val liveTweets: MutableLiveData<List<Tweet>?> = MutableLiveData()
    val liveKeywords: MutableLiveData<List<String>?> = MutableLiveData()
    val liveState: MutableLiveData<TweetNetworkModelState> = MutableLiveData(TweetNetworkModelState.NeverFetched)

    var tempQuery: String? = null

    fun tweetsSearch(q: String) {
        // 検索中ならすぐ返す
        if (liveState.value == TweetNetworkModelState.Fetching) return

        liveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {

            val token = AccessTokenRepository().getAccessToken()
            if (token.isNullOrEmpty()) {
                val error = Throwable("authentication error")
                liveState.postValue(TweetNetworkModelState.FetchedError(error))
                return@launch
            }

            val list = TweetsSearchRepository().tweetsSearch(token, q, FIRST_PAGE_SIZE, liveState)
            if (!list.isNullOrEmpty()) {
                liveTweets.postValue(list)
            }

            tempQuery = q
            // キーワード履歴に入れる
            KeywordsRepository().saveKeyword(q)
        }
    }

    fun nextTweetsSearch() {
        val q = tempQuery ?: return
        val token = Token.accessToken ?: return
        val tweet = liveTweets.value?.last() ?: return // 現行の最古のツイート

        if (liveState.value == TweetNetworkModelState.Fetching) return
        liveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {
            val list =
                TweetsSearchRepository().nextTweetsSearch(token, q, tweet.id, FIRST_PAGE_SIZE, liveState)
            if (list.isNullOrEmpty()) return@launch

            val newList = mutableListOf<Tweet>()
            newList.addAll(liveTweets.value!!)

            // 現行の最古のツイートと次候補の先頭のツイートが同じ場合、現行の最古のツイートを消す。
            if (list.first().id == tweet.id) {
                newList.remove(tweet)
            }
            newList.addAll(list)

            liveTweets.postValue(newList)
        }
    }

    fun loadKeywordsHistory() {
        viewModelScope.launch {
            val keywords = KeywordsRepository().getRecentKeywords()
            liveKeywords.postValue(keywords)
        }
    }

    companion object {
        const val FIRST_PAGE_SIZE = 15
    }
}