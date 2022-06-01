package com.example.tweetssearch.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.model.User
import com.example.tweetssearch.util.TweetUtil
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun TweetDetailView(tweet: Tweet) {
    MdcTheme {
        Column(
        ) {
            Row(
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(tweet.user.profile_image_url_https)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.no_image),
                    contentDescription = "user icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = tweet.user.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "@${tweet.user.screen_name}",
                        style = MaterialTheme.typography.body1,
                    )
                }
            }
            Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
                Text(
                    text = tweet.text,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = TweetUtil().convertCreatedAt(tweet.createdAt),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Preview("Light Theme")
@Composable
fun TweetDetailPreviewLight() {
    TweetDetailView(
        tweet = Tweet(
            text = "Hello!! aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            createdAt = "Thu April 29 13:50:48 +0000 2022",
            id = 1,
            user = User("hoge", "foo", "")
        )
    )
}

@Preview("Dark Theme")
@Composable
fun TweetDetailPreviewDark() {
    TweetDetailView(
        tweet = Tweet(
            text = "Hello!! aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            createdAt = "Thu April 29 13:50:48 +0000 2022",
            id = 1,
            user = User("hoge", "foo", "")
        )
    )
}