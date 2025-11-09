package com.xly.business.square.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jspp.model.UserCard
import com.xly.R
import com.xly.business.square.model.Matchmaker
import com.xly.business.square.view.adapter.BlurTransformation
import com.xly.databinding.ItemMatchmakerInfoBinding
import com.xly.databinding.ItemMatchmakerUserBinding

/**
 * 列表项类型
 */
sealed class MatchmakerListItem {
    data class MatchmakerInfo(val matchmaker: Matchmaker) : MatchmakerListItem()
    data class UserInfo(val userCard: UserCard) : MatchmakerListItem()
}

class MatchmakerUserAdapter(
    private val onUserItemClick: (UserCard) -> Unit
) : ListAdapter<MatchmakerListItem, RecyclerView.ViewHolder>(MatchmakerListItemDiffCallback()) {

    companion object {
        private const val TYPE_MATCHMAKER_INFO = 0
        private const val TYPE_USER_INFO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MatchmakerListItem.MatchmakerInfo -> TYPE_MATCHMAKER_INFO
            is MatchmakerListItem.UserInfo -> TYPE_USER_INFO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MATCHMAKER_INFO -> {
                val binding = ItemMatchmakerInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MatchmakerInfoViewHolder(binding)
            }
            TYPE_USER_INFO -> {
                val binding = ItemMatchmakerUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MatchmakerUserViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MatchmakerListItem.MatchmakerInfo -> {
                (holder as MatchmakerInfoViewHolder).bind(item.matchmaker)
            }
            is MatchmakerListItem.UserInfo -> {
                (holder as MatchmakerUserViewHolder).bind(item.userCard, onUserItemClick)
            }
        }
    }

    /**
     * 红娘信息 ViewHolder
     */
    class MatchmakerInfoViewHolder(
        private val binding: ItemMatchmakerInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(matchmaker: Matchmaker) {
            // 头像
            Glide.with(binding.root.context)
                .load(matchmaker.avatar)
                .placeholder(R.mipmap.head_img)
                .circleCrop()
                .into(binding.ivMatchmakerAvatar)

            // 姓名
            binding.tvMatchmakerName.text = matchmaker.name

            // 认证标识
            binding.ivVerified.visibility = if (matchmaker.isVerified) View.VISIBLE else View.GONE

            // VIP标识
            binding.tvVIP.visibility = if (matchmaker.isVIP) View.VISIBLE else View.GONE

            // 评分
            binding.tvRating.text = String.format("%.1f", matchmaker.rating)

            // 用户数量
            binding.tvUserCount.text = "${matchmaker.userCount}位用户"

            // 位置
            binding.tvLocation.text = "📍 ${matchmaker.location}"

            // 简介
            binding.tvDescription.text = matchmaker.description

            // 成功率
            binding.tvSuccessRate.text = "成功率：${matchmaker.successRate.toInt()}%"

            // 从业经验
            if (matchmaker.yearsOfExperience > 0) {
                binding.tvExperience.text = "${matchmaker.yearsOfExperience}年从业经验"
                binding.tvExperience.visibility = View.VISIBLE
            } else {
                binding.tvExperience.visibility = View.GONE
            }

            // 标签
            setupTags(matchmaker.tags)
        }

        private fun setupTags(tags: List<String>) {
            binding.llTags.removeAllViews()
            if (tags.isNotEmpty()) {
                tags.take(3).forEach { tag ->
                    val tagView = LayoutInflater.from(binding.root.context)
                        .inflate(R.layout.item_tag, binding.llTags, false)
                    val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
                    tvTag.text = tag
                    binding.llTags.addView(tagView)
                }
            }
        }
    }

    /**
     * 用户信息 ViewHolder
     */
    class MatchmakerUserViewHolder(
        private val binding: ItemMatchmakerUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userCard: UserCard, onItemClickListener: (UserCard) -> Unit) {
            // 加载模糊背景（使用头像进行模糊处理）
            Glide.with(binding.root.context)
                .asBitmap()
                .load(userCard.avatarUrl)
                .placeholder(R.mipmap.head_img)
                .transform(BlurTransformation(binding.root.context, 25f))
                .into(binding.ivBlurBackground)

            // 姓名
            binding.tvName.text = userCard.name

            // 年龄
            binding.tvAge.text = "${userCard.age}岁"

            // 位置
            binding.tvLocation.text = "📍 ${userCard.location}"

            // 职业
            if (userCard.occupation.isNotEmpty()) {
                binding.tvOccupation.text = userCard.occupation
                binding.tvOccupation.visibility = View.VISIBLE
            } else {
                binding.tvOccupation.visibility = View.GONE
            }

            // 学历
            if (userCard.education.isNotEmpty()) {
                binding.tvEducation.text = userCard.education
                binding.tvEducation.visibility = View.VISIBLE
            } else {
                binding.tvEducation.visibility = View.GONE
            }

            // 标签
            setupTags(userCard.tags)

            // 红娘推荐评语
            setupRecommendation(userCard)

            // 点击事件
            binding.root.setOnClickListener {
                onItemClickListener(userCard)
            }
        }

        private fun setupRecommendation(userCard: UserCard) {
            // 如果有推荐评语，显示推荐区域
            // 这里可以使用userCard的某个字段，或者从其他地方获取
            // 暂时使用bio字段作为推荐评语，如果为空则不显示
            val recommendationText = getRecommendationText(userCard)
            if (recommendationText.isNotEmpty()) {
                binding.llRecommendation.visibility = View.VISIBLE
                binding.divider.visibility = View.VISIBLE
                binding.tvRecommendation.text = "红娘推荐：$recommendationText"
            } else {
                binding.llRecommendation.visibility = View.GONE
                binding.divider.visibility = View.GONE
            }
        }

        private fun getRecommendationText(userCard: UserCard): String {
            // 如果有专门的推荐评语字段，使用该字段
            // 这里暂时使用bio字段，后续可以扩展UserCard添加recommendationComment字段
            return userCard.bio.takeIf { it.isNotEmpty() } ?: ""
        }

        private fun setupTags(tags: List<String>) {
            binding.llTags.removeAllViews()
            if (tags.isNotEmpty()) {
                binding.llTags.visibility = View.VISIBLE
                tags.take(2).forEach { tag -> // 最多显示2个标签
                    val tagView = LayoutInflater.from(binding.root.context)
                        .inflate(R.layout.item_tag, binding.llTags, false)
                    val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
                    tvTag.text = tag
                    binding.llTags.addView(tagView)
                }
            } else {
                binding.llTags.visibility = View.GONE
            }
        }
    }

    class MatchmakerListItemDiffCallback : DiffUtil.ItemCallback<MatchmakerListItem>() {
        override fun areItemsTheSame(
            oldItem: MatchmakerListItem,
            newItem: MatchmakerListItem
        ): Boolean {
            return when {
                oldItem is MatchmakerListItem.MatchmakerInfo && newItem is MatchmakerListItem.MatchmakerInfo ->
                    oldItem.matchmaker.id == newItem.matchmaker.id
                oldItem is MatchmakerListItem.UserInfo && newItem is MatchmakerListItem.UserInfo ->
                    oldItem.userCard.id == newItem.userCard.id
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: MatchmakerListItem,
            newItem: MatchmakerListItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
