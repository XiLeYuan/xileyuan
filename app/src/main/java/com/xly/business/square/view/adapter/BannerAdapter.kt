package com.xly.business.square.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.R

data class BannerItem(
    val id: String,
    val imageResId: Int, // 图片资源ID
    val title: String? = null, // 可选标题
    val url: String? = null // 可选跳转链接
)

class BannerAdapter(
    private val banners: List<BannerItem>,
    private val onBannerClick: (BannerItem) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner: ImageView = itemView.findViewById(R.id.ivBanner)
        val tvBannerTitle: TextView = itemView.findViewById(R.id.tvBannerTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        
        // 加载图片
        Glide.with(holder.ivBanner)
            .load(banner.imageResId)
            .centerCrop()
            .into(holder.ivBanner)

        // 显示标题（如果有）
        if (!banner.title.isNullOrEmpty()) {
            holder.tvBannerTitle.text = banner.title
            holder.tvBannerTitle.visibility = View.VISIBLE
        } else {
            holder.tvBannerTitle.visibility = View.GONE
        }

        // 点击事件
        holder.itemView.setOnClickListener {
            onBannerClick(banner)
        }
    }

    override fun getItemCount(): Int = banners.size
}

