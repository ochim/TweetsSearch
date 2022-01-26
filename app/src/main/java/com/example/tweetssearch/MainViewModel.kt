package com.example.tweetssearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tweetssearch.model.Token
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.TweetNetworkModelState
import com.example.tweetssearch.repository.AccessTokenRepository
import com.example.tweetssearch.repository.KeywordsRepository
import com.example.tweetssearch.repository.TweetsRemoteDataSource
import com.example.tweetssearch.repository.TweetsSearchRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val tweetsSearchRepository: TweetsSearchRepository = TweetsSearchRepository(
        TweetsRemoteDataSource()
    ),
    private val keywordsRepository: KeywordsRepository = KeywordsRepository(),
    private val accessTokenRepository: AccessTokenRepository = AccessTokenRepository()
) : ViewModel() {

    private val mLiveKeywords: MutableLiveData<List<String>?> = MutableLiveData()
    private val mLiveState: MutableLiveData<TweetNetworkModelState<List<Tweet>>> =
        MutableLiveData(TweetNetworkModelState.NeverFetched)

    val liveKeywords: LiveData<List<String>?> get() = mLiveKeywords
    val liveState: LiveData<TweetNetworkModelState<List<Tweet>>> get() = mLiveState

    private var nowTweets: List<Tweet>? = null
    private var nowQuery: String? = null

    fun tweetsSearch(q: String) {
        // 検索中ならすぐ返す
        if (mLiveState.value == TweetNetworkModelState.Fetching) return

        mLiveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {

            val token = accessTokenRepository.getAccessToken()
            if (token.isNullOrEmpty()) {
                mLiveState.postValue(
                    TweetNetworkModelState.FetchedError(Exception("authentication error"))
                )
                return@launch
            }

            try {
                val list =
                    tweetsSearchRepository.tweetsSearch(token, q, FIRST_PAGE_SIZE, null)
                if (!list.isNullOrEmpty()) {
                    mLiveState.postValue(TweetNetworkModelState.FetchedOK(list))
                    nowTweets = list
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
        val token = Token.accessToken ?: return
        val tweet = nowTweets?.last() ?: return // 現行の最古のツイート

        if (mLiveState.value == TweetNetworkModelState.Fetching) return
        mLiveState.value = TweetNetworkModelState.Fetching

        viewModelScope.launch {
            var list: List<Tweet>? = null

            try {
                list =
                    tweetsSearchRepository.tweetsSearch(
                        token,
                        q,
                        FIRST_PAGE_SIZE,
                        tweet.id
                    )

            } catch (e: Exception) {
                mLiveState.postValue(TweetNetworkModelState.FetchedError(e))
            }

            if (list.isNullOrEmpty()) return@launch

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
            mLiveKeywords.postValue(keywords)
        }
    }

    companion object {
        const val FIRST_PAGE_SIZE = 15
    }

}

class MainViewModelFactory(
    private val tweetsSearchRepository: TweetsSearchRepository,
    private val keywordsRepository: KeywordsRepository,
    private val accessTokenRepository: AccessTokenRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(tweetsSearchRepository, keywordsRepository, accessTokenRepository) as T
    }
}