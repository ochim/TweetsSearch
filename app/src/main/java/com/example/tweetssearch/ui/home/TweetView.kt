package com.example.tweetssearch.ui.home

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Patterns
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.util.TweetUtil
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun TweetCell(tweet: Tweet) {
    MdcTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(tweet.user.profile_image_url_https)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.no_image),
                contentDescription = "user icon",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
                Text(
                    text = "${tweet.user.name} @${tweet.user.screen_name}",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = TweetUtil().convertCreatedAt(tweet.createdAt),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DefaultLinkifyText(
                    text = tweet.text
                )
            }
        }
    }
}

@Composable
// https://stackoverflow.com/a/68670583/11658192
fun DefaultLinkifyText(
    modifier: Modifier = Modifier,
    text: String?,
    textAppearance: Int = android.R.style.TextAppearance_Material_Body2
) {
    val context = LocalContext.current
    val customLinkifyTextView = remember {
        TextView(context)
    }
    AndroidView(modifier = modifier, factory = { customLinkifyTextView }) { textView ->
        textView.setTextAppearance(textAppearance)
        textView.text = text ?: ""
        LinkifyCompat.addLinks(textView, Linkify.ALL)
        Linkify.addLinks(
            textView, Patterns.PHONE, "tel:",
            Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter
        )
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
