package com.xly.business.square.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xly.databinding.ItemTodaySelectionUserBinding
import com.xly.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.xly.business.square.model.TodaySelectionUser

class CherryPickAdapter(
    private val onItemClick: (TodaySelectionUser) -> Unit,
    private val onLikeClick: (TodaySelectionUser) -> Unit
) : ListAdapter<TodaySelectionUser, CherryPickAdapter.SelectionViewHolder>(
    SelectionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val binding = ItemTodaySelectionUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onLikeClick)
    }

    class SelectionViewHolder(
        private val binding: ItemTodaySelectionUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            user: TodaySelectionUser,
            onItemClick: (TodaySelectionUser) -> Unit,
            onLikeClick: (TodaySelectionUser) -> Unit
        ) {
            // 头像 - 加载本地 mipmap 资源
            val context = binding.root.context
            val resourceId = context.resources.getIdentifier(
                user.avatar,
                "mipmap",
                context.packageName
            )
            if (resourceId != 0) {
                Glide.with(context)
                    .load(resourceId)
                    .centerCrop()
                    .into(binding.ivAvatar)
            } else {
                // 如果资源不存在，使用默认头像
                binding.ivAvatar.setImageResource(R.mipmap.head_img)
            }

            // 姓名
            binding.tvUserName.text = user.name

            // 年龄
            binding.tvAge.text = "${user.age}岁"

            // 位置
            binding.tvLocation.text = "📍 ${user.location}"

            // 精选理由（如果有）


            // 精选描述
            binding.tvSelectionDescription.text = user.selectionDescription

            // 标签
//            setupTags(binding.llTags, user.tags)

            // 匹配度（如果有）
            user.matchScore?.let { score ->
                binding.tvSelectionDescription.text =
                    "${user.selectionDescription}（匹配度：${score.toInt()}%）"
            }

            // 点击事件
            binding.root.setOnClickListener {
                onItemClick(user)
            }


        }

        private fun setupTags(container: ViewGroup, tags: List<String>) {
            container.removeAllViews()

            tags.take(4).forEach { tag ->
                val tagView = TextView(container.context).apply {
                    text = tag
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(
                        context,
                        R.color.text_secondary
                    ))
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

    class SelectionDiffCallback : DiffUtil.ItemCallback<TodaySelectionUser>() {
        override fun areItemsTheSame(
            oldItem: TodaySelectionUser,
            newItem: TodaySelectionUser
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TodaySelectionUser,
            newItem: TodaySelectionUser
        ): Boolean {
            return oldItem == newItem
        }
    }
}