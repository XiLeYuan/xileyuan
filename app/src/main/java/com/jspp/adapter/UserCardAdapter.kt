package com.jspp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.xly.R
import com.jspp.model.UserCard
import com.xly.middlelibrary.utils.click

class UserCardAdapter(
    private val onCardClickListener: (UserCard, View) -> Unit
) : RecyclerView.Adapter<UserCardAdapter.UserCardViewHolder>() {

    private val cards = mutableListOf<UserCard>()

    fun setCards(newCards: List<UserCard>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    fun getUserCard(position: Int): UserCard? {
        return if (position in 0 until cards.size) {
            cards[position]
        } else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_card, parent, false)
        return UserCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserCardViewHolder, position: Int) {
        val userCard = cards[position]
        holder.bind(userCard)

        if (position == 0) {
            holder.ivBackground.setImageResource(R.mipmap.find_img_3)
        } else if (position == 1) {
            holder.ivBackground.setImageResource(R.mipmap.find_img_2)
        } else if (position == 2) {
            holder.ivBackground.setImageResource(R.mipmap.find_img_4)
        } else if (position == 3) {
            holder.ivBackground.setImageResource(R.mipmap.find_img_1)
        } else {
            holder.ivBackground.setImageResource(R.mipmap.find_img_4)
        }
        // 设置点击事件
        holder.ivBackground.click { view ->
            onCardClickListener(userCard, view)
        }
        holder.arrowRightIv.click {
            onCardClickListener(userCard, holder.ivBackground)
        }
    }

    override fun getItemCount(): Int = cards.size

    class UserCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivBackground: ImageView = itemView.findViewById(R.id.ivBackground)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAge: TextView = itemView.findViewById(R.id.tvAge)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvBio: TextView = itemView.findViewById(R.id.tvBio)
        val tagContainer: FlexboxLayout = itemView.findViewById(R.id.tagContainer)
        val arrowRightIv: ImageView = itemView.findViewById(R.id.arrowRightIv)


        fun bind(userCard: UserCard) {
            // 加载头像
            Glide.with(itemView.context)
                .load(userCard.avatarUrl)
                .circleCrop()
                .into(ivAvatar)

            // 设置用户信息
            tvName.text = userCard.name
            tvAge.text = "${userCard.age}岁"
            tvLocation.text = userCard.location
            tvBio.text = userCard.bio

            // 设置标签
            setupTags(userCard.tags)

            // 设置共享元素转场名称
            itemView.transitionName = "user_card"
        }

        private fun setupTags(tags: List<String>) {
            tagContainer.removeAllViews()

            tags.forEach { tag ->
                val tagView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_tag, tagContainer, false)

                val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
                tvTag.text = tag

                tagContainer.addView(tagView)
            }
        }
    }
}