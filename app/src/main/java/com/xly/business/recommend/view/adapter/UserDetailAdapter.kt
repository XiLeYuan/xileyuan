package com.xly.business.recommend.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.R

class UserDetailAdapter(
    private val headerData: List<String>, // 头部卡片内容
    private val contentData: List<String> // 普通内容
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_content, parent, false)
            ContentViewHolder(view)
        }
    }

    override fun getItemCount(): Int = 1 + contentData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(headerData)
        } else if (holder is ContentViewHolder) {
            holder.bind(contentData[position - 1])
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: List<String>) {
            // 你可以用itemView.findViewById<TextView>(...)设置内容
            // 这里假设item_card_header.xml里是静态内容，如需动态可用ViewGroup.addView等
        }
    }

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(content: String) {
            itemView.findViewById<TextView>(R.id.tvContent).text = content
        }
    }
}