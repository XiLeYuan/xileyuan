package com.xly.business.square.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jspp.model.UserCard
import com.xly.R
import com.xly.databinding.ItemMatchmakerUserBinding
import com.xly.middlelibrary.utils.LYUtils

class MatchmakerUserAdapter(
    private val onUserItemClick: (UserCard) -> Unit
) : ListAdapter<UserCard, MatchmakerUserAdapter.MatchmakerUserViewHolder>(UserCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchmakerUserViewHolder {
        val binding = ItemMatchmakerUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchmakerUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchmakerUserViewHolder, position: Int) {
        holder.bind(getItem(position), onUserItemClick)
    }

    /**
     * 用户信息 ViewHolder
     */
    class MatchmakerUserViewHolder(
        private val binding: ItemMatchmakerUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userCard: UserCard, onItemClickListener: (UserCard) -> Unit) {
            // 生成随机颜色并模糊处理作为背景
            setupBlurBackground(userCard)

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

        private fun setupBlurBackground(userCard: UserCard) {
            // 根据用户ID生成稳定的随机颜色（相同用户总是相同颜色）
            val random = java.util.Random(userCard.id.hashCode().toLong())
            val color = android.graphics.Color.rgb(
                random.nextInt(180) + 50,  // 50-230，避免太暗或太亮
                random.nextInt(180) + 50,
                random.nextInt(180) + 50
            )
            
            // 获取屏幕宽度和合适的背景高度
            val displayMetrics = binding.root.context.resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = (200 * displayMetrics.density).toInt() // 200dp转px，足够覆盖item
            
            // 创建颜色Bitmap
            val bitmap = LYUtils.createColorBitmap(color, width, height)
            
            // 对颜色Bitmap进行模糊处理（blurBitmap会修改原始bitmap并返回）
            // RenderScript 的模糊半径必须在 0-25 之间
            val blurredBitmap = LYUtils.blurBitmap(binding.root.context, bitmap, 25f)
            
            // 显示模糊后的颜色背景
            binding.ivBlurBackground.setImageBitmap(blurredBitmap)
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

    class UserCardDiffCallback : DiffUtil.ItemCallback<UserCard>() {
        override fun areItemsTheSame(oldItem: UserCard, newItem: UserCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserCard, newItem: UserCard): Boolean {
            return oldItem == newItem
        }
    }
}
