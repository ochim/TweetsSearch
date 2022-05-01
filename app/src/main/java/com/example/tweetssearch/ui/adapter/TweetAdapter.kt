package com.example.tweetssearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.ui.home.TweetCell

class TweetAdapter(
    private var dataset: List<Tweet> = emptyList(),
    private val onClick: (Tweet) -> Unit
) : RecyclerView.Adapter<TweetAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tweetCell: ComposeView = view.findViewById(R.id.tweet_cell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_tweet, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val tweet = dataset[position]
        holder.tweetCell.setContent { 
            TweetCell(tweet)
        }
        holder.view.setOnClickListener { onClick(tweet) }
    }

    override fun getItemCount() = dataset.size

    fun updateDataSet(data: List<Tweet>) {
        dataset = data
        notifyDataSetChanged()
    }
}
