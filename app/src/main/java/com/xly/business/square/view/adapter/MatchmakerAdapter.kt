package com.xly.business.square.view.adapter


import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.databinding.ItemMatchmakerCardBinding
import com.xly.R
import com.xly.business.square.model.Matchmaker

class MatchmakerAdapter(
    private val onItemClick: (Matchmaker) -> Unit,
    private val onViewDetailsClick: (Matchmaker) -> Unit,
    private val onContactClick: (Matchmaker) -> Unit
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
        holder.bind(getItem(position), onItemClick, onViewDetailsClick, onContactClick)
    }

    class MatchmakerViewHolder(
        private val binding: ItemMatchmakerCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            matchmaker: Matchmaker,
            onItemClick: (Matchmaker) -> Unit,
            onViewDetailsClick: (Matchmaker) -> Unit,
            onContactClick: (Matchmaker) -> Unit
        ) {
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

            // 评分 - 使用1个icon显示，根据评分绘制颜色
            val orangeColor = ContextCompat.getColor(binding.root.context, R.color.brand_orange)
            val grayColor = ContextCompat.getColor(binding.root.context, R.color.text_hint)
            // 根据评分设置icon颜色（评分>=4.0显示橘色，否则显示灰色）
            binding.ivRatingIcon.setColorFilter(
                if (matchmaker.rating >= 4.0f) orangeColor else grayColor
            )
            
            // 评分文本
            binding.tvRating.text = String.format("%.1f", matchmaker.rating)

            // 用户数量 - 突出显示数字
            setupUserCount(binding.tvUserCount, matchmaker.userCount)

            // 服务区域
            binding.tvLocation.text = matchmaker.location

            // 简介
            binding.tvDescription.text = matchmaker.description

            // 标签
            setupTags(binding.llTags, matchmaker.tags)

            // 查看详情按钮点击事件
            binding.llViewDetails.setOnClickListener {
                onViewDetailsClick(matchmaker)
            }


            // 点击事件（点击卡片其他区域）
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

        private fun setupRatingStars(container: ViewGroup, rating: Float) {
            val orangeColor = ContextCompat.getColor(container.context, R.color.brand_orange)
            val grayColor = ContextCompat.getColor(container.context, R.color.text_hint)
            
            // 计算应该填充的星星数量（0-5）
            val filledStars = (rating / 1.0f).toInt().coerceIn(0, 5)
            val hasHalfStar = (rating - filledStars) >= 0.5f
            
            container.children.forEachIndexed { index, view ->
                if (view is ImageView) {
                    when {
                        index < filledStars -> {
                            // 完全填充的星星 - 橘色
                            view.setColorFilter(orangeColor)
                        }
                        index == filledStars && hasHalfStar -> {
                            // 半颗星星 - 可以考虑用半透明橘色或者保持灰色
                            // 这里简化处理，如果有半颗星，也显示为橘色
                            view.setColorFilter(orangeColor)
                        }
                        else -> {
                            // 未填充的星星 - 灰色
                            view.setColorFilter(grayColor)
                        }
                    }
                }
            }
        }

        private fun setupUserCount(textView: TextView, userCount: Int) {
            // 数字和文案之间保持一个空格
            val text = "${userCount} 位用户"
            val spannableString = SpannableString(text)
            
            // 找到数字的起始和结束位置
            val numberStart = 0
            val numberEnd = userCount.toString().length
            
            // 设置数字颜色为橘色
            val orangeColor = ContextCompat.getColor(textView.context, R.color.brand_orange)
            spannableString.setSpan(
                ForegroundColorSpan(orangeColor),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // 设置数字字体大小（增大1.3倍）
            spannableString.setSpan(
                RelativeSizeSpan(1.3f),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // 设置数字字体为斜体加粗
            spannableString.setSpan(
                StyleSpan(android.graphics.Typeface.BOLD_ITALIC),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            textView.text = spannableString
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