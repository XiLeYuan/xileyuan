package com.xly.business.square.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.imageview.ShapeableImageView
import com.xly.business.square.model.Moment
import com.xly.R
import com.xly.business.square.view.MomentImageDetailActivity
import com.xly.middlelibrary.utils.click

class MomentAdapter(
    private val list: List<Moment>,
    private val activity: Activity,
    private val bannerList: List<BannerItem>? = null,
    private val onBannerClick: ((BannerItem) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_MOMENT = 1
    }

    class MomentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.avatar)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val content: TextView = itemView.findViewById(R.id.content)
        val imageContainer: FlexboxLayout = itemView.findViewById(R.id.imageContainer)
        val time: TextView = itemView.findViewById(R.id.time)
        val btnLike: ImageView = itemView.findViewById(R.id.btnLike)
        val btnComment: ImageView = itemView.findViewById(R.id.btnComment)
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bannerScrollView: HorizontalScrollView = itemView.findViewById(R.id.bannerScrollView)
        val bannerContainer: LinearLayout = itemView.findViewById(R.id.bannerContainer)
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasBanner() && position == 0) {
            VIEW_TYPE_BANNER
        } else {
            VIEW_TYPE_MOMENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_BANNER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_banner_header, parent, false)
                BannerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_moment, parent, false)
                MomentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannerViewHolder -> {
                // 设置 Banner
                if (bannerList != null && bannerList.isNotEmpty()) {
                    // 清空容器
                    holder.bannerContainer.removeAllViews()
                    
                    // 获取 banner 宽度和间距
                    val bannerWidth = holder.itemView.resources.getDimensionPixelSize(R.dimen.banner_item_width)
                    val pageMargin = holder.itemView.resources.getDimensionPixelSize(R.dimen.banner_page_margin)
                    
                    // 动态添加 banner items
                    bannerList.forEachIndexed { index, banner ->
                        val bannerView = LayoutInflater.from(holder.itemView.context)
                            .inflate(R.layout.item_banner, holder.bannerContainer, false)
                        
                        // 设置宽度
                        val layoutParams = LinearLayout.LayoutParams(
                            bannerWidth,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        if (index < bannerList.size - 1) {
                            layoutParams.marginEnd = pageMargin
                        }
                        bannerView.layoutParams = layoutParams
                        
                        // 绑定数据
                        val ivBanner: ImageView = bannerView.findViewById(R.id.ivBanner)
                        Glide.with(ivBanner)
                            .load(banner.imageResId)
                            .centerCrop()
                            .into(ivBanner)
                        
                        // 设置点击事件
                        bannerView.setOnClickListener {
                            onBannerClick?.invoke(banner)
                        }
                        
                        holder.bannerContainer.addView(bannerView)
                    }
                }
            }
            is MomentViewHolder -> {
                val momentPosition = if (hasBanner()) position - 1 else position
                val moment = list[momentPosition]
                holder.userName.text = moment.userName
                holder.content.text = moment.content
                holder.time.text = moment.time
                Glide.with(holder.avatar).load(moment.userAvatar).into(holder.avatar)

                // 动态添加图片
                holder.imageContainer.removeAllViews()
                // 在 MomentAdapter 的 onBindViewHolder 中
                moment.images.forEachIndexed { idx, imageResId ->
                    val img = ShapeableImageView(holder.itemView.context)
                    val size = holder.itemView.resources.displayMetrics.widthPixels / 4
                    val lp = FlexboxLayout.LayoutParams(size, size)
                    lp.setMargins(4, 4, 4, 4)
                    img.layoutParams = lp
                    img.scaleType = ImageView.ScaleType.CENTER_CROP
                    img.transitionName = "moment_image_${moment.id}_$idx"

                    img.shapeAppearanceModel = img.shapeAppearanceModel
                        .toBuilder()
                        .setAllCornerSizes(18f) // 16f px, 也可以用 TypedValue.applyDimension
                        .build()
                    Glide.with(img).load(imageResId).into(img)
                    img.setOnClickListener {
                        // 只传递单张图片
                        val intent = Intent(holder.itemView.context, MomentImageDetailActivity::class.java)
                        intent.putExtra("imageResId", imageResId) // 只传一张图片的 res id
                        intent.putExtra("momentId", moment.id)
                        intent.putExtra("imageIndex", idx)

                        // 创建共享元素动画
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            activity,
                            Pair.create(img as View, img.transitionName)
                        )
                        activity.startActivity(intent, options.toBundle())
                    }
                    holder.imageContainer.addView(img)
                }
                holder.btnLike.click {
                    holder.btnLike.setImageResource(R.mipmap.zan_select)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size + if (hasBanner()) 1 else 0
    }

    private fun hasBanner(): Boolean {
        return bannerList != null && bannerList.isNotEmpty()
    }
}