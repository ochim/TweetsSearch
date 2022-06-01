package com.example.tweetssearch.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.example.tweetssearch.ui.home.DefaultLinkifyText
import com.example.tweetssearch.util.TweetUtil
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun TweetDetailView(tweet: Tweet) {
    MdcTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
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
                DefaultLinkifyText(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = tweet.text,
                    textAppearance = android.R.style.TextAppearance_Material_Headline,
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

private val testTweet = Tweet(
    text = "Qiita https://qiita.com/kazu_developer/items/975c8595c318b0a1c68b eget elit sed nunc suscipit vestibulum in ut mi. Curabitur ac nibh eget dui auctor vulputate. Nulla facilisi. Praesent sit amet ante dolor. Maecenas facilisis ultricies odio, in blandit purus rhoncus nec. Sed dictum metus sit amet neque gravida, eget maximus neque ornare. Integer sit amet quam suscipit, convallis nisi in, blandit mi. Aenean pretium felis ut semper rutrum. Ut nec ligula vitae sapien semper ultricies vitae nec est. Suspendisse at dictum turpis, vel volutpat neque. Fusce sit amet enim pulvinar, vestibulum elit in, accumsan turpis. Praesent consectetur, magna ac ultrices eleifend, sapien neque egestas est, et tincidunt nibh nibh non ipsum. Nulla suscipit leo diam, sit amet rhoncus elit pulvinar sit amet. Nulla eget elit sed nunc suscipit vestibulum in ut mi. Curabitur ac nibh eget dui auctor vulputate. Nulla facilisi. Praesent sit amet ante dolor. Maecenas facilisis ultricies odio, in blandit purus rhoncus nec. Sed dictum metus sit amet neque gravida, eget maximus neque ornare. Integer sit amet quam suscipit, convallis nisi in, blandit mi. Aenean pretium felis ut semper rutrum. Ut nec ligula vitae sapien semper ultricies vitae nec est. Suspendisse at dictum turpis, vel volutpat neque. Fusce sit amet enim pulvinar, vestibulum elit in, accumsan turpis. Praesent consectetur, magna ac ultrices eleifend, sapien neque egestas est, et tincidunt nibh nibh non ipsum. Nulla suscipit leo diam, sit amet rhoncus elit pulvinar sit amet.",
    createdAt = "Thu April 29 13:50:48 +0000 2022",
    id = 1,
    user = User("hoge", "foo", "")
)

@Preview("Light Theme")
@Composable
fun TweetDetailPreviewLight() {
    TweetDetailView(testTweet)
}

@Preview("Dark Theme")
@Composable
fun TweetDetailPreviewDark() {
    TweetDetailView(testTweet)
}