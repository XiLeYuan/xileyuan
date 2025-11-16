package com.xly.business.square.view.adapter

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.R
import com.xly.business.recommend.view.HometownFragment
import com.xly.business.square.model.Matchmaker
import com.xly.business.square.view.adapter.BlurTransformation
import com.xly.databinding.ItemHometownUserBinding
import com.xly.databinding.ItemMatchmakerHeaderBinding

class MatchmakerUserResourcesAdapter(
    private val matchmaker: Matchmaker,
    private val onUserClick: (HometownFragment.HometownUser, View?) -> Unit,
    private val onHeaderCreated: ((ItemMatchmakerHeaderBinding) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_USER = 1
    }

    private val users = mutableListOf<HometownFragment.HometownUser>()

    fun submitList(newUsers: List<HometownFragment.HometownUser>) {
        val diffCallback = UserDiffCallback(users, newUsers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users.clear()
        users.addAll(newUsers)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemMatchmakerHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                onHeaderCreated?.invoke(binding)
                HeaderViewHolder(binding, matchmaker)
            }
            else -> {
                val binding = ItemHometownUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserViewHolder(binding, onUserClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind()
            is UserViewHolder -> holder.bind(users[position - 1])
        }
    }

    override fun getItemCount(): Int = 1 + users.size

    class HeaderViewHolder(
        private val binding: ItemMatchmakerHeaderBinding,
        private val matchmaker: Matchmaker
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val context = binding.root.context

            // 设置红娘头像
            val avatarResourceId = context.resources.getIdentifier(
                matchmaker.avatar,
                "mipmap",
                context.packageName
            )
            if (avatarResourceId != 0) {
                Glide.with(context)
                    .load(avatarResourceId)
                    .circleCrop()
                    .into(binding.ivMatchmakerAvatar)
            } else {
                binding.ivMatchmakerAvatar.setImageResource(R.mipmap.head_img)
            }

            // 设置模糊背景
            val blurResourceId = context.resources.getIdentifier(
                matchmaker.avatar,
                "mipmap",
                context.packageName
            )
            if (blurResourceId != 0) {
                Glide.with(context)
                    .load(blurResourceId)
                    .transform(BlurTransformation(context, 25f))
                    .into(binding.ivBlurBackground)
            } else {
                Glide.with(context)
                    .load(R.mipmap.head_img)
                    .transform(BlurTransformation(context, 25f))
                    .into(binding.ivBlurBackground)
            }

            // 设置红娘信息
            binding.tvMatchmakerName.text = matchmaker.name
            binding.tvMatchmakerLocation.text = matchmaker.location
            binding.tvMatchmakerRating.text = "⭐ ${String.format("%.1f", matchmaker.rating)}分"
            binding.tvMatchmakerDescription.text = matchmaker.description
            // 设置简介字体为 SERIF（与红娘列表 item 一致）
            binding.tvMatchmakerDescription.typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.NORMAL
            )
            
            // 设置用户数量 - 数字使用橘色加粗斜体
            setupUserCount(binding.tvUserCount, matchmaker.userCount)

            // 设置标签
            binding.llMatchmakerTags.removeAllViews()
            if (matchmaker.tags.isNotEmpty()) {
                binding.llMatchmakerTags.visibility = View.VISIBLE
                matchmaker.tags.forEach { tag ->
                    if (tag.isNotEmpty()) {
                        val tagView = LayoutInflater.from(context)
                            .inflate(R.layout.item_tag, binding.llMatchmakerTags, false)
                        val tagTextView = tagView.findViewById<TextView>(R.id.tvTag)
                        tagTextView.text = tag
                        binding.llMatchmakerTags.addView(tagView)
                    }
                }
            } else {
                binding.llMatchmakerTags.visibility = View.GONE
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
                StyleSpan(Typeface.BOLD_ITALIC),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            textView.text = spannableString
        }
    }

    class UserViewHolder(
        private val binding: ItemHometownUserBinding,
        private val onUserClick: (HometownFragment.HometownUser, View?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: HometownFragment.HometownUser) {
            val context = binding.root.context

            // 加载头像
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
                binding.ivAvatar.setImageResource(R.mipmap.head_img)
            }

            // 设置转场动画名称
            binding.ivAvatar.transitionName = "user_avatar_${user.id}"

            // 设置姓名和年龄
            binding.tvName.text = user.name
            binding.tvAge.text = "${user.age}岁"

            // 点击事件
            binding.root.setOnClickListener {
                onUserClick(user, binding.ivAvatar)
            }
        }
    }

    private class UserDiffCallback(
        private val oldList: List<HometownFragment.HometownUser>,
        private val newList: List<HometownFragment.HometownUser>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}

