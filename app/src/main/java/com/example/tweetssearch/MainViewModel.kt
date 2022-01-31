package com.example.tweetssearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tweetssearch.model.Token
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.repository.AccessTokenRepository
import com.example.tweetssearch.repository.KeywordsRepository
import com.example.tweetssearch.repository.TweetsRemoteDataSource
import com.example.tweetssearch.repository.TweetsSearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val tweetsSearchRepository: TweetsSearchRepository = TweetsSearchRepository(
        TweetsRemoteDataSource()
    ),
    private val keywordsRepository: KeywordsRepository = KeywordsRepository(),
    private val accessTokenRepository: AccessTokenRepository = AccessTokenRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TweetsUiState())
    val uiState: StateFlow<TweetsUiState> = _uiState.asStateFlow()

    private val _keywordsState = MutableStateFlow(KeywordsState())
    val keywordsState: StateFlow<KeywordsState> = _keywordsState.asStateFlow()

    private var nowTweets: List<Tweet>? = null
    private var nowQuery: String? = null

    fun tweetsSearch(q: String) {

        // 検索中ならすぐ返す
        if (_uiState.value.fetchState == FetchState.FETCHING) return

        _uiState.update { it.copy(fetchState = FetchState.FETCHING) }

        viewModelScope.launch {

            val token = accessTokenRepository.getAccessToken()
            if (token.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        userMessage = "authentication error",
                        fetchState = FetchState.FETCHED_ERROR
                    )
                }
                return@launch
            }

            try {
                val list =
                    tweetsSearchRepository.tweetsSearch(token, q, FIRST_PAGE_SIZE, null)
                if (!list.isNullOrEmpty()) {
                    _uiState.update {
                        it.copy(tweetsItems = list, fetchState = FetchState.FETCHED_OK)
                    }
                    nowTweets = list
                } else {
                    _uiState.update {
                        it.copy(userMessage = "empty", fetchState = FetchState.FETCHED_ERROR)
                    }
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(userMessage = e.message, fetchState = FetchState.FETCHED_ERROR)
                }
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

        if (_uiState.value.fetchState == FetchState.FETCHING) return

        _uiState.update { it.copy(fetchState = FetchState.FETCHING) }

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
                _uiState.update {
                    it.copy(userMessage = e.message, fetchState = FetchState.FETCHED_ERROR)
                }
                return@launch
            }

            if (list.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(tweetsItems = emptyList(), fetchState = FetchState.FETCHED_OK)
                }
                return@launch
            }

            val newList = nowTweets!!.toMutableList()

            // 現行の最古のツイートと次候補の先頭のツイートが同じ場合、現行の最古のツイートを消す。
            if (list.first().id == tweet.id) {
                newList.remove(tweet)
            }
            newList.addAll(list)

            nowTweets = newList.toList()
            _uiState.update {
                it.copy(tweetsItems = newList, fetchState = FetchState.FETCHED_OK)
            }
        }
    }

    fun loadKeywordsHistory() {
        viewModelScope.launch {
            val keywords = keywordsRepository.getRecentKeywords()
            _keywordsState.update {
                it.copy(
                    keywordsItems = keywords ?: emptyList()
                )
            }
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