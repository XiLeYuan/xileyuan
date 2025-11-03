package com.xly.business.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xly.databinding.ItemUserDetailSectionBinding

class UserDetailAdapter(
    private val items: List<String>
) : ListAdapter<String, UserDetailAdapter.DetailViewHolder>(DetailDiffCallback()) {

    init {
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemUserDetailSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetailViewHolder(
        private val binding: ItemUserDetailSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.tvSectionTitle.text = title
            // 根据标题设置内容
            binding.tvSectionContent.text = getContentForTitle(title)
        }

        private fun getContentForTitle(title: String): String {
            return when (title) {
                "基本信息" -> "教育背景、职业信息等"
                "兴趣爱好" -> "旅行、摄影、音乐、阅读"
                "理想对象" -> "希望找到一个志同道合的伴侣"
                "生活状态" -> "积极向上，热爱生活"
                "联系方式" -> "通过平台私信联系"
                else -> ""
            }
        }
    }

    class DetailDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}