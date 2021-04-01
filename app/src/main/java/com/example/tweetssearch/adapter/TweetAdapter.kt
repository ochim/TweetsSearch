package com.example.tweetssearch.adapter

import android.content.Context
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tweetssearch.R
import com.example.tweetssearch.model.Tweet
import com.example.tweetssearch.util.TweetUtil

class TweetAdapter(
    private val context: Context,
    private var dataset: List<Tweet> = emptyList(),
    private val onClick: (Tweet) -> Unit
) : RecyclerView.Adapter<TweetAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_text)
        val createdAtView: TextView = view.findViewById(R.id.tv_created_at)
        val nameView: TextView = view.findViewById(R.id.tv_user_name)
        val profileImageView: ImageView = view.findViewById(R.id.iv_profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_tweet, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val tweet = dataset[position]
        holder.nameView.text = "${tweet.user.name}  @${tweet.user.screen_name}"
        holder.textView.text = tweet.text
        holder.createdAtView.text = TweetUtil().convertCreatedAt(tweet.createdAt)
        holder.view.setOnClickListener { onClick(tweet) }

        if (!tweet.user.profile_image_url_https.isNullOrEmpty()) {
            // 角を丸くする
            holder.profileImageView.outlineProvider = object : ViewOutlineProvider() {

                override fun getOutline(view: View?, outline: Outline?) {
                    view ?: return
                    outline?.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        16F
                    )
                    view.clipToOutline = true
                }
            }
            Glide.with(context)
                .load(tweet.user.profile_image_url_https)
                .centerCrop()
                .error(R.drawable.no_image)
                .into(holder.profileImageView)
        }
    }

    override fun getItemCount() = dataset.size

    fun updateDataSet(data: List<Tweet>) {
        dataset = data
        notifyDataSetChanged()
    }
}