package com.xly.business.square.view.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.databinding.ItemMatchmakerCardBinding
import com.xly.R
import com.xly.business.square.model.Matchmaker

class MatchmakerAdapter(
    private val onItemClick: (Matchmaker) -> Unit
) : ListAdapter<Matchmaker, MatchmakerAdapter.MatchmakerViewHolder>(MatchmakerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchmakerViewHolder {
        val binding = ItemMatchmakerCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchmakerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchmakerViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class MatchmakerViewHolder(
        private val binding: ItemMatchmakerCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(matchmaker: Matchmaker, onItemClick: (Matchmaker) -> Unit) {
            // 头像 - 加载本地 mipmap 资源
            val context = binding.root.context
            val resourceId = context.resources.getIdentifier(
                matchmaker.avatar,
                "mipmap",
                context.packageName
            )
            if (resourceId != 0) {
                Glide.with(context)
                    .load(resourceId)
                    .circleCrop()
                    .into(binding.ivAvatar)
            } else {
                // 如果资源不存在，使用默认头像
                binding.ivAvatar.setImageResource(R.mipmap.head_img)
            }

            // 姓名
            binding.tvName.text = matchmaker.name

            // 认证标识
            binding.ivVerified.visibility =
                if (matchmaker.isVerified) View.VISIBLE else View.GONE

            // 评分
            binding.tvRating.text = String.format("%.1f", matchmaker.rating)

            // 用户数量
            binding.tvUserCount.text = "${matchmaker.userCount}位用户"

            // VIP标识
            binding.tvVIP.visibility =
                if (matchmaker.isVIP) View.VISIBLE else View.GONE

            // 服务区域
            binding.tvLocation.text = "📍 ${matchmaker.location}"

            // 简介
            binding.tvDescription.text = matchmaker.description

            // 标签
            setupTags(binding.llTags, matchmaker.tags)

            // 成功率
            binding.tvSuccessRate.text = "成功率：${matchmaker.successRate.toInt()}%"

            // 点击事件
            binding.root.setOnClickListener {
                onItemClick(matchmaker)
            }
        }

        private fun setupTags(container: ViewGroup, tags: List<String>) {
            container.removeAllViews()

            tags.take(3).forEach { tag ->
                val tagView = TextView(container.context).apply {
                    text = tag
                    textSize = 11f
                    background = ContextCompat.getDrawable(
                        context,
                        R.drawable.tag_background
                    )
                    setPadding(8.dpToPx(), 4.dpToPx(), 8.dpToPx(), 4.dpToPx())

                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 6.dpToPx()
                    }
                }
                container.addView(tagView)
            }
        }

        private fun Int.dpToPx(): Int {
            return (this * binding.root.context.resources.displayMetrics.density).toInt()
        }
    }

    class MatchmakerDiffCallback : DiffUtil.ItemCallback<Matchmaker>() {
        override fun areItemsTheSame(oldItem: Matchmaker, newItem: Matchmaker): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Matchmaker, newItem: Matchmaker): Boolean {
            return oldItem == newItem
        }
    }
}