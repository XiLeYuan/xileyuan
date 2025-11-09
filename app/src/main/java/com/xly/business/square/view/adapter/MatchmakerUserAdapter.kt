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
import com.xly.databinding.ItemMatchmakerUserBinding

class MatchmakerUserAdapter(
    private val onItemClickListener: (UserCard) -> Unit
) : ListAdapter<UserCard, MatchmakerUserAdapter.MatchmakerUserViewHolder>(MatchmakerUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchmakerUserViewHolder {
        val binding = ItemMatchmakerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchmakerUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchmakerUserViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickListener)
    }

    class MatchmakerUserViewHolder(
        private val binding: ItemMatchmakerUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userCard: UserCard, onItemClickListener: (UserCard) -> Unit) {
            // 加载头像
            Glide.with(binding.root.context)
                .load(userCard.avatarUrl)
                .placeholder(R.mipmap.head_img)
                .circleCrop()
                .into(binding.ivAvatar)

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

            // 在线状态
            binding.vOnlineStatus.visibility = if (userCard.isOnline) View.VISIBLE else View.GONE

            // 标签
            setupTags(userCard.tags)

            // 点击事件
            binding.root.setOnClickListener {
                onItemClickListener(userCard)
            }
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

    class MatchmakerUserDiffCallback : DiffUtil.ItemCallback<UserCard>() {
        override fun areItemsTheSame(oldItem: UserCard, newItem: UserCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserCard, newItem: UserCard): Boolean {
            return oldItem == newItem
        }
    }
}
