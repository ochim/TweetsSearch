package com.example.tweetssearch.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet
import com.google.android.material.composethemeadapter.MdcTheme

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cv: ComposeView = view.findViewById(R.id.compose_view)
        cv.setContent {
            MdcTheme {
                DetailView(args.argTweet)
            }
        }
    }

    companion object {
    }
}

@Composable
fun DetailView(tweet: Tweet) {
    Text(text = tweet.text)
}
