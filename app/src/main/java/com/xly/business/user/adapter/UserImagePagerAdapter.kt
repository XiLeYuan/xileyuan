package com.xly.business.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.xly.databinding.ItemUserDetailImageBinding

/**
 * 用户详情页图片适配器（ViewPager2）
 */
class UserImagePagerAdapter(
    private val images: List<String>
) : ListAdapter<String, UserImagePagerAdapter.ImageViewHolder>(ImageDiffCallback()) {

    init {
        submitList(images)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemUserDetailImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(
        private val binding: ItemUserDetailImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            /*Glide.with(binding.root.context)
                .load(imageUrl)
                .centerCrop()
                .into(binding.ivUserImage)*/

            binding.ivUserImage.setImageResource(R.mipmap.find_img_2)
        }

    }

    class ImageDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
