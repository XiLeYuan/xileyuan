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
        
        // 设置箭头按钮点击事件（保留）
        holder.arrowRightIv.click {
            onCardClickListener(userCard, holder.ivBackground)
        }
    }

    override fun getItemCount(): Int = cards.size

    class UserCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivBackground: ImageView = itemView.findViewById(R.id.ivBackground)
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        val tvNameAge: TextView = itemView.findViewById(R.id.tvNameAge)
        val verifyIv: ImageView = itemView.findViewById(R.id.verifyIv)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvBio: TextView = itemView.findViewById(R.id.tvBio)
        val tagContainer: FlexboxLayout = itemView.findViewById(R.id.tagContainer)
        val arrowRightIv: ImageView = itemView.findViewById(R.id.arrowRightIv)
        val llLocationContainer: android.widget.LinearLayout = itemView.findViewById(R.id.llLocationContainer)
        val llHometown: android.widget.LinearLayout = itemView.findViewById(R.id.llHometown)
        val llResidence: android.widget.LinearLayout = itemView.findViewById(R.id.llResidence)
        val tvHometown: TextView = itemView.findViewById(R.id.tvHometown)
        val tvResidence: TextView = itemView.findViewById(R.id.tvResidence)
        val tvHometownDot: TextView = itemView.findViewById(R.id.tvHometownDot)
        val tvResidenceDot: TextView = itemView.findViewById(R.id.tvResidenceDot)


        fun bind(userCard: UserCard) {
            // 头像暂时弃用，不加载
            // ivAvatar.visibility = View.GONE

            // 设置用户信息：姓名和年龄用逗号连接
            tvNameAge.text = "${userCard.name}，${userCard.age}岁"
            
            // 设置认证标识显示（暂时默认显示，后续可以从userCard添加isVerified字段）
            verifyIv.visibility = View.VISIBLE
            
            tvLocation.text = userCard.location
            tvBio.text = userCard.bio

            // 设置家乡和居住地标签
            setupLocationTags(userCard.hometown, userCard.residence)

            // 设置标签
            setupTags(userCard.tags)

            // 设置共享元素转场名称（使用背景图片，与精选列表保持一致）
            ivBackground.transitionName = "user_avatar_${userCard.id}"
        }

        private fun setupLocationTags(hometown: String, residence: String) {
            // 设置家乡
            if (hometown.isNotEmpty()) {
                llHometown.visibility = View.VISIBLE
                tvHometown.text = hometown
                tvHometownDot.visibility = View.VISIBLE
            } else {
                llHometown.visibility = View.GONE
                tvHometownDot.visibility = View.GONE
            }
            
            // 设置现居地
            if (residence.isNotEmpty()) {
                llResidence.visibility = View.VISIBLE
                tvResidence.text = residence
                tvResidenceDot.visibility = View.VISIBLE
            } else {
                llResidence.visibility = View.GONE
                tvResidenceDot.visibility = View.GONE
            }
            
            // 如果家乡和现居地都为空，隐藏整个容器
            if (hometown.isEmpty() && residence.isEmpty()) {
                llLocationContainer.visibility = View.GONE
            } else {
                llLocationContainer.visibility = View.VISIBLE
            }
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