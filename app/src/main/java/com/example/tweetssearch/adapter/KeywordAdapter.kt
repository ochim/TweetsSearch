package com.example.tweetssearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tweetssearch.R

class KeywordAdapter(
    private val context: Context,
    private var dataset: List<String> = emptyList(),
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<KeywordAdapter.ItemViewHolder>() {

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_keyword)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.list_keyword, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val keyword = dataset[position]
        holder.textView.text = keyword
        holder.view.setOnClickListener { onClick(keyword) }
    }

    override fun getItemCount() = dataset.size

    fun updateDataSet(data: List<String>) {
        dataset = data
        notifyDataSetChanged()
    }

}