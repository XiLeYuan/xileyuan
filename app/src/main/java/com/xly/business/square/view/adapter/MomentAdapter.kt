package com.xly.business.square.view.adapter

import android.app.Activity
import android.content.Context
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

                // 使用新的布局管理器动态添加图片
                setupImages(holder, moment)
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

    /**
     * 设置图片布局
     */
    private fun setupImages(holder: MomentViewHolder, moment: Moment) {
        holder.imageContainer.removeAllViews()
        
        val imageCount = moment.images.size
        if (imageCount == 0) return
        
        val screenWidth = holder.itemView.resources.displayMetrics.widthPixels
        val context = holder.itemView.context
        val layoutConfigs = MomentImageLayoutManager.getLayoutConfig(context, imageCount, screenWidth)
        val displayCount = MomentImageLayoutManager.getDisplayCount(imageCount)
        val showMore = MomentImageLayoutManager.shouldShowMoreIndicator(imageCount)
        
        // 设置FlexboxLayout的方向和换行
        holder.imageContainer.flexDirection = com.google.android.flexbox.FlexDirection.ROW
        holder.imageContainer.flexWrap = com.google.android.flexbox.FlexWrap.WRAP
        holder.imageContainer.justifyContent = com.google.android.flexbox.JustifyContent.FLEX_START
        
        // 添加图片
        for (i in 0 until displayCount) {
            val isLastImage = i == displayCount - 1
            val shouldShowMoreOnLast = isLastImage && showMore
            
            if (shouldShowMoreOnLast) {
                // 最后一张显示"更多"标识
                addMoreIndicator(holder, moment, layoutConfigs[i], imageCount - displayCount + 1, displayCount)
            } else {
                // 正常显示图片
                addImage(holder, moment, moment.images[i], i, layoutConfigs[i])
            }
        }
    }

    /**
     * 添加单张图片
     */
    private fun addImage(
        holder: MomentViewHolder,
        moment: Moment,
        imageResId: Int,
        index: Int,
        imageSize: MomentImageLayoutManager.ImageSize
    ) {
        val img = ShapeableImageView(holder.itemView.context)
        val lp = FlexboxLayout.LayoutParams(imageSize.width, imageSize.height)
        lp.setMargins(4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context))
        img.layoutParams = lp
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.transitionName = "moment_image_${moment.id}_$index"

        img.shapeAppearanceModel = img.shapeAppearanceModel
            .toBuilder()
            .setAllCornerSizes(12f.dpToPx(holder.itemView.context))
            .build()
        
        Glide.with(img).load(imageResId).into(img)
        
        img.setOnClickListener {
            val intent = Intent(holder.itemView.context, MomentImageDetailActivity::class.java)
            intent.putExtra("imageResId", imageResId)
            intent.putExtra("momentId", moment.id)
            intent.putExtra("imageIndex", index)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                Pair.create(img as View, img.transitionName)
            )
            activity.startActivity(intent, options.toBundle())
        }
        
        holder.imageContainer.addView(img)
    }

    /**
     * 添加"更多"标识
     */
    private fun addMoreIndicator(
        holder: MomentViewHolder,
        moment: Moment,
        imageSize: MomentImageLayoutManager.ImageSize,
        moreCount: Int,
        displayCount: Int
    ) {
        // 创建容器View
        val container = android.widget.FrameLayout(holder.itemView.context)
        val lp = FlexboxLayout.LayoutParams(imageSize.width, imageSize.height)
        lp.setMargins(4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context), 4.dpToPx(holder.itemView.context))
        container.layoutParams = lp
        
        // 添加第6张图片（索引5）作为背景
        val backgroundImageIndex = displayCount - 1 // 第6张图片的索引
        val backgroundImg = ShapeableImageView(holder.itemView.context)
        backgroundImg.layoutParams = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        backgroundImg.scaleType = ImageView.ScaleType.CENTER_CROP
        backgroundImg.shapeAppearanceModel = backgroundImg.shapeAppearanceModel
            .toBuilder()
            .setAllCornerSizes(12f.dpToPx(holder.itemView.context))
            .build()
        
        Glide.with(backgroundImg).load(moment.images[backgroundImageIndex]).into(backgroundImg)
        container.addView(backgroundImg)
        
        // 添加"更多"遮罩层
        val moreView = LayoutInflater.from(holder.itemView.context)
            .inflate(R.layout.item_moment_image_more, container, false)
        val moreLp = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        moreView.layoutParams = moreLp
        val tvMoreCount = moreView.findViewById<TextView>(R.id.tvMoreCount)
        tvMoreCount.text = "+$moreCount"
        container.addView(moreView)
        
        // 点击事件：跳转到图片详情页，显示所有图片
        container.setOnClickListener {
            val intent = Intent(holder.itemView.context, MomentImageDetailActivity::class.java)
            intent.putExtra("momentId", moment.id)
            intent.putExtra("imageIndex", backgroundImageIndex)
            activity.startActivity(intent)
        }
        
        holder.imageContainer.addView(container)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun Float.dpToPx(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}