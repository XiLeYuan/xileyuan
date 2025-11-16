package com.xly.business.square.view.adapter


import android.view.LayoutInflater
import android.view.View
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
import com.xly.middlelibrary.utils.LYFontUtil

class CherryPickAdapter(
    private val onItemClick: (TodaySelectionUser, android.view.View) -> Unit,
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
            onItemClick: (TodaySelectionUser, android.view.View) -> Unit,
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

            // 设置转场动画名称
            binding.ivAvatar.transitionName = "user_avatar_${user.id}"

            // 姓名和年龄合并显示，用逗号隔开
            binding.tvNameAge.text = "${user.name}，${user.age}岁"
            // 设置字体
            binding.tvNameAge.typeface = LYFontUtil.getMediumFont(binding.root.context)

            // 认证标识显示（暂时默认显示，后续可以从user添加isVerified字段控制）
            binding.ivVerified.visibility = View.VISIBLE

            // 家乡和现居地
            if (user.hometown.isNotEmpty()) {
                binding.llHometown.visibility = View.VISIBLE
                binding.tvHometown.text = user.hometown
                binding.tvHometownDot.visibility = View.VISIBLE
            } else {
                binding.llHometown.visibility = View.GONE
                binding.tvHometownDot.visibility = View.GONE
            }

            if (user.residence.isNotEmpty()) {
                binding.llResidence.visibility = View.VISIBLE
                binding.tvResidence.text = user.residence
                binding.tvResidenceDot.visibility = View.VISIBLE
            } else {
                binding.llResidence.visibility = View.GONE
                binding.tvResidenceDot.visibility = View.GONE
            }

            // 如果家乡和现居地都为空，隐藏整个容器
            if (user.hometown.isEmpty() && user.residence.isEmpty()) {
                binding.llLocationContainer.visibility = View.GONE
            } else {
                binding.llLocationContainer.visibility = View.VISIBLE
            }

            // 精选特色标签（右上角）
            if (user.featureTags.isNotEmpty()) {
                binding.llFeatureTags.visibility = View.VISIBLE
                setupFeatureTags(binding.llFeatureTags, user.featureTags)
            } else {
                binding.llFeatureTags.visibility = View.GONE
            }

            // 标签（显示学历、收入、颜值、身高等关键信息）
            if (user.tags.isNotEmpty()) {
                binding.llTags.visibility = View.VISIBLE
                setupTags(binding.llTags, user.tags)
            } else {
                binding.llTags.visibility = View.GONE
            }

            // 送花/送爱心入口点击事件
            binding.ivFlowIcon.setOnClickListener {
                onLikeClick(user)
            }

            // 向下箭头按钮点击事件 - 进入详情页
            binding.ivArrowDown.setOnClickListener {
                onItemClick(user, binding.ivAvatar)
            }

            // 点击事件 - 传递头像 View 用于转场动画
            binding.root.setOnClickListener {
                onItemClick(user, binding.ivAvatar)
            }


        }

        private fun setupFeatureTags(container: ViewGroup, tags: List<String>) {
            container.removeAllViews()

            if (tags.isNotEmpty()) {
                // 将多个标签合并成一个文本，用圆点分隔
                val mergedText = tags.joinToString(" · ")
                val tagView = TextView(container.context).apply {
                    text = mergedText
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(
                        context,
                        R.color.bright_green
                    ))
                    background = ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_feature_tag_black_transparent
                    )
                    setPadding(10.dpToPx(), 4.dpToPx(), 10.dpToPx(), 4.dpToPx())
                    setTypeface(typeface, android.graphics.Typeface.BOLD)

                    layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                container.addView(tagView)
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
                        android.R.color.white
                    ))
                    background = ContextCompat.getDrawable(
                        context,
                        R.drawable.tag_background_black_transparent
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