package com.example.tweetssearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet

class TweetAdapter(private val context: Context, private var dataset: List<Tweet> = listOf(), private val onClick: (Tweet) -> Unit)
    : RecyclerView.Adapter<TweetAdapter.ItemViewHolder>()  {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_text)
        val createdAtView: TextView = view.findViewById(R.id.item_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_tweet, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val tweet = dataset[position]
        holder.textView.text = tweet.text
        holder.createdAtView.text = tweet.createdAt
        holder.view.setOnClickListener{ onClick(tweet) }
    }

    override fun getItemCount() = dataset.size

    fun updateDataSet(data: List<Tweet>) {
        dataset = data
        notifyDataSetChanged()
    }
 }