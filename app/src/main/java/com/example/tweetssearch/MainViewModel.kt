package com.example.tweetssearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.repository.AccessTokenRepository
import com.example.tweetssearch.repository.TweetsSearchRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val liveTweets: MutableLiveData<List<Tweet>?> = MutableLiveData()

    fun tweetsSearch(q: String) {
        viewModelScope.launch {
            val token = AccessTokenRepository().getAccessToken()
            token ?: return@launch

            val list = TweetsSearchRepository().tweetsSearch(token, q)
            liveTweets.postValue(list)
        }
    }
}