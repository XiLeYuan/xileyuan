package com.jspp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.jspp.model.UserCard
import com.xly.R
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

        // 加载背景图片 - 使用本地资源名称
        val context = holder.itemView.context
        val resourceId = context.resources.getIdentifier(
            userCard.avatarUrl, // 使用avatarUrl字段存储背景图片资源名称
            "mipmap",
            context.packageName
        )
        if (resourceId != 0) {
            // 本地资源
            Glide.with(context)
                .load(resourceId)
                .centerCrop()
                .into(holder.ivBackground)
        } else {
            // 如果资源不存在，使用默认背景
            holder.ivBackground.setImageResource(R.mipmap.find_img_3)
        }
        
        // 设置点击事件（传递背景图片View用于转场动画）
        holder.ivBackground.click { view ->
            onCardClickListener(userCard, holder.ivBackground)
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
            // 头像暂时弃用，不加载
            // ivAvatar.visibility = View.GONE

            // 设置用户信息
            tvName.text = userCard.name
            tvAge.text = "${userCard.age}岁" + " * " + userCard.location
            tvLocation.text = userCard.location
            tvBio.text = userCard.bio

            // 设置标签
            setupTags(userCard.tags)

            // 设置共享元素转场名称（使用背景图片，与精选列表保持一致）
            ivBackground.transitionName = "user_avatar_${userCard.id}"
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