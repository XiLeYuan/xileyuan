package com.xly.business.recommend.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.xly.business.recommend.model.UserDetailItem

class UserDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<UserDetailItem>()

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_INFO = 1
        private const val TYPE_HOBBY = 2
        private const val TYPE_BIO = 3
    }

    fun setData(newItems: List<UserDetailItem>) {
        items.clear()
        items.addAll(newItems)
        Log.i("UserDetailAdapter", "setData: items.size=${items.size}")
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            UserDetailItem.TYPE_HEADER -> TYPE_HEADER
            UserDetailItem.TYPE_INFO -> TYPE_INFO
            UserDetailItem.TYPE_HOBBY -> TYPE_HOBBY
            UserDetailItem.TYPE_BIO -> TYPE_BIO
            else -> TYPE_INFO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_detail_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_INFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_detail_info, parent, false)
                InfoViewHolder(view)
            }
            TYPE_HOBBY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_detail_hobby, parent, false)
                HobbyViewHolder(view)
            }
            TYPE_BIO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_detail_bio, parent, false)
                BioViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_detail_info, parent, false)
                InfoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is InfoViewHolder -> holder.bind(item)
            is HobbyViewHolder -> holder.bind(item)
            is BioViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // Header ViewHolder
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

        fun bind(item: UserDetailItem) {
            tvTitle.text = item.title
        }
    }

    // Info ViewHolder
    inner class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLabel: TextView = itemView.findViewById(R.id.tv_label)
        private val tvValue: TextView = itemView.findViewById(R.id.tv_value)

        fun bind(item: UserDetailItem) {
            tvLabel.text = item.title
            tvValue.text = item.content
        }
    }

    // Hobby ViewHolder
    inner class HobbyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHobby: TextView = itemView.findViewById(R.id.tv_hobby)

        fun bind(item: UserDetailItem) {
            tvHobby.text = item.content
        }
    }

    // Bio ViewHolder
    inner class BioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBio: TextView = itemView.findViewById(R.id.tv_bio)

        fun bind(item: UserDetailItem) {
            tvBio.text = item.content
        }
    }
}