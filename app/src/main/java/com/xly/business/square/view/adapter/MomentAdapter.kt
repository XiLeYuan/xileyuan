package com.xly.business.square.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.imageview.ShapeableImageView
import com.xly.business.square.model.Moment
import com.xly.R
import com.xly.business.square.view.MomentImageDetailActivity
import com.xly.business.square.view.MomentVideoPlayerActivity
import com.xly.middlelibrary.utils.click

class MomentAdapter(
    private var list: MutableList<Moment>,
    private val activity: Activity,
    private val bannerList: List<BannerItem>? = null,
    private val onBannerClick: ((BannerItem) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_MOMENT = 1
    }
    
    // 视频播放器管理：key为momentId，value为ExoPlayer
    private val videoPlayers = mutableMapOf<String, ExoPlayer>()
    
    // 当前正在播放的视频ID
    private var currentPlayingVideoId: String? = null

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
     * 更新数据（用于下拉刷新）
     */
    fun updateData(newList: List<Moment>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    
    /**
     * 追加数据（用于加载更多）
     */
    fun addData(newList: List<Moment>) {
        val startPosition = list.size + if (hasBanner()) 1 else 0
        list.addAll(newList)
        notifyItemRangeInserted(startPosition, newList.size)
    }
    
    /**
     * 获取当前数据列表
     */
    fun getDataList(): List<Moment> {
        return list.toList()
    }

    /**
     * 设置图片布局
     */
    private fun setupImages(holder: MomentViewHolder, moment: Moment) {
        holder.imageContainer.removeAllViews()
        
        // 如果有视频，显示视频
        if (!moment.videoUrl.isNullOrEmpty()) {
            setupVideo(holder, moment)
            return
        }
        
        val imageCount = moment.images.size
        if (imageCount == 0) return
        
        val screenWidth = holder.itemView.resources.displayMetrics.widthPixels
        val context = holder.itemView.context
        
        // 判断是否为竖图（单张图片时使用）
        val isVertical = moment.isVertical
        
        val layoutConfigs = MomentImageLayoutManager.getLayoutConfig(context, imageCount, screenWidth, isVertical)
        val displayCount = MomentImageLayoutManager.getDisplayCount(imageCount)
        val showMore = MomentImageLayoutManager.shouldShowMoreIndicator(imageCount)
        
        // 设置FlexboxLayout的方向和换行
        holder.imageContainer.flexDirection = com.google.android.flexbox.FlexDirection.ROW
        holder.imageContainer.flexWrap = com.google.android.flexbox.FlexWrap.WRAP
        holder.imageContainer.justifyContent = com.google.android.flexbox.JustifyContent.FLEX_START
        
        when (displayCount) {
            1 -> {
                // 单张图片
                addImage(holder, moment, moment.images[0], 0, layoutConfigs[0])
            }
            2 -> {
                // 两张图片：第一张右上和右下无圆角，第二张左上和左下无圆角
                addImage(holder, moment, moment.images[0], 0, layoutConfigs[0], isFirstOfTwo = true)
                addImage(holder, moment, moment.images[1], 1, layoutConfigs[1], isSecondOfTwo = true)
            }
            3 -> {
                // 三张图片：第一张最大，第二三张在右侧上下排列
                setupThreeImagesLayout(holder, moment, layoutConfigs, showMore, imageCount)
            }
        }
    }

    /**
     * 设置三张图片的特殊布局
     */
    private fun setupThreeImagesLayout(
        holder: MomentViewHolder,
        moment: Moment,
        layoutConfigs: List<MomentImageLayoutManager.ImageSize>,
        showMore: Boolean,
        totalImageCount: Int
    ) {
        val context = holder.itemView.context
        val horizontalMargin = 1.dpToPx(context) // 横向间距（减小）
        val verticalMargin = 2.dpToPx(context) // 纵向间距（统一）
        
        // 第一张图片（左侧，最大）：右上和右下不要圆角
        // 只设置上下和左边的间距，右边间距为横向间距
        val firstImg = ShapeableImageView(context)
        val firstLp = FlexboxLayout.LayoutParams(layoutConfigs[0].width, layoutConfigs[0].height)
        firstLp.setMargins(verticalMargin, verticalMargin, horizontalMargin, verticalMargin)
        firstImg.layoutParams = firstLp
        firstImg.scaleType = ImageView.ScaleType.CENTER_CROP
        firstImg.transitionName = "moment_image_${moment.id}_0"
        
        val cornerSize = 8f.dpToPx(context)
        firstImg.shapeAppearanceModel = firstImg.shapeAppearanceModel.toBuilder()
            .setTopLeftCornerSize(cornerSize)
            .setTopRightCornerSize(0f)
            .setBottomLeftCornerSize(cornerSize)
            .setBottomRightCornerSize(0f)
            .build()
        
        Glide.with(firstImg).load(moment.images[0]).into(firstImg)
        firstImg.setOnClickListener {
            val intent = Intent(context, MomentImageDetailActivity::class.java)
            intent.putExtra("imageResId", moment.images[0])
            intent.putExtra("momentId", moment.id)
            intent.putExtra("imageIndex", 0)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                Pair.create(firstImg as View, firstImg.transitionName)
            )
            activity.startActivity(intent, options.toBundle())
        }
        holder.imageContainer.addView(firstImg)
        
        // 右侧容器：包含第二张和第三张图片
        val rightContainer = LinearLayout(context)
        rightContainer.orientation = LinearLayout.VERTICAL
        val rightContainerLp = FlexboxLayout.LayoutParams(
            layoutConfigs[1].width,
            layoutConfigs[0].height // 高度等于第一张图片的高度
        )
        rightContainerLp.setMargins(horizontalMargin, verticalMargin, verticalMargin, verticalMargin)
        rightContainer.layoutParams = rightContainerLp
        
        // 第二张图片：左上、左下、右下不要圆角
        val secondImg = createImageViewWithCustomCorners(holder, moment, moment.images[1], 1, layoutConfigs[1],
            topLeft = false, topRight = true, bottomLeft = false, bottomRight = false)
        val secondLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            layoutConfigs[1].height
        )
        secondLp.bottomMargin = verticalMargin // 使用统一的纵向间距
        secondImg.layoutParams = secondLp
        rightContainer.addView(secondImg)
        
        // 第三张图片（可能带"更多"标识）：左上、左下、右上不要圆角
        val thirdImgContainer = android.widget.FrameLayout(context)
        val thirdImgContainerLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            layoutConfigs[2].height
        )
        thirdImgContainer.layoutParams = thirdImgContainerLp
        
        val thirdImg = createImageViewWithCustomCorners(holder, moment, moment.images[2], 2, layoutConfigs[2],
            topLeft = false, topRight = false, bottomLeft = false, bottomRight = true)
        val thirdImgLp = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        )
        thirdImg.layoutParams = thirdImgLp
        thirdImgContainer.addView(thirdImg)
        
        // 如果超过3张，在第三张图片右下角显示剩余数量
        if (showMore) {
            val moreCount = totalImageCount - 3
            val badgeView = LayoutInflater.from(context)
                .inflate(R.layout.item_moment_image_count_badge, thirdImgContainer, false)
            val badgeLp = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
            )
            badgeLp.gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            badgeLp.setMargins(8.dpToPx(context), 8.dpToPx(context), 8.dpToPx(context), 8.dpToPx(context))
            badgeView.layoutParams = badgeLp
            
            val tvImageCount = badgeView.findViewById<TextView>(R.id.tvImageCount)
            tvImageCount.text = "+$moreCount"
            
            thirdImgContainer.addView(badgeView)
        }
        
        rightContainer.addView(thirdImgContainer)
        holder.imageContainer.addView(rightContainer)
    }

    /**
     * 创建ImageView（不添加到容器）
     */
    private fun createImageView(
        holder: MomentViewHolder,
        moment: Moment,
        imageResId: Int,
        index: Int,
        imageSize: MomentImageLayoutManager.ImageSize
    ): ShapeableImageView {
        return createImageViewWithCustomCorners(holder, moment, imageResId, index, imageSize,
            topLeft = true, topRight = true, bottomLeft = true, bottomRight = true)
    }

    /**
     * 创建ImageView（不添加到容器），支持自定义圆角
     */
    private fun createImageViewWithCustomCorners(
        holder: MomentViewHolder,
        moment: Moment,
        imageResId: Int,
        index: Int,
        imageSize: MomentImageLayoutManager.ImageSize,
        topLeft: Boolean,
        topRight: Boolean,
        bottomLeft: Boolean,
        bottomRight: Boolean
    ): ShapeableImageView {
        val img = ShapeableImageView(holder.itemView.context)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.transitionName = "moment_image_${moment.id}_$index"

        val cornerSize = 8f.dpToPx(holder.itemView.context) // 减小圆角
        val builder = img.shapeAppearanceModel.toBuilder()
        
        builder.setTopLeftCornerSize(if (topLeft) cornerSize else 0f)
            .setTopRightCornerSize(if (topRight) cornerSize else 0f)
            .setBottomLeftCornerSize(if (bottomLeft) cornerSize else 0f)
            .setBottomRightCornerSize(if (bottomRight) cornerSize else 0f)
        
        img.shapeAppearanceModel = builder.build()
        
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
        
        return img
    }

    /**
     * 添加图片，支持自定义圆角
     */
    private fun addImageWithCustomCorners(
        holder: MomentViewHolder,
        moment: Moment,
        imageResId: Int,
        index: Int,
        imageSize: MomentImageLayoutManager.ImageSize,
        topLeft: Boolean,
        topRight: Boolean,
        bottomLeft: Boolean,
        bottomRight: Boolean
    ) {
        val img = ShapeableImageView(holder.itemView.context)
        val lp = FlexboxLayout.LayoutParams(imageSize.width, imageSize.height)
        val horizontalMargin = 1.dpToPx(holder.itemView.context) // 横向间距（减小）
        val verticalMargin = 2.dpToPx(holder.itemView.context) // 纵向间距（统一）
        lp.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)
        img.layoutParams = lp
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.transitionName = "moment_image_${moment.id}_$index"

        val cornerSize = 8f.dpToPx(holder.itemView.context) // 减小圆角
        val builder = img.shapeAppearanceModel.toBuilder()
        
        builder.setTopLeftCornerSize(if (topLeft) cornerSize else 0f)
            .setTopRightCornerSize(if (topRight) cornerSize else 0f)
            .setBottomLeftCornerSize(if (bottomLeft) cornerSize else 0f)
            .setBottomRightCornerSize(if (bottomRight) cornerSize else 0f)
        
        img.shapeAppearanceModel = builder.build()
        
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
     * 添加单张图片
     */
    private fun addImage(
        holder: MomentViewHolder,
        moment: Moment,
        imageResId: Int,
        index: Int,
        imageSize: MomentImageLayoutManager.ImageSize,
        isFirstOfTwo: Boolean = false,
        isSecondOfTwo: Boolean = false
    ) {
            val img = ShapeableImageView(holder.itemView.context)
        val lp = FlexboxLayout.LayoutParams(imageSize.width, imageSize.height)
        val horizontalMargin = 1.dpToPx(holder.itemView.context) // 横向间距（减小）
        val verticalMargin = 2.dpToPx(holder.itemView.context) // 纵向间距（统一）
        lp.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)
            img.layoutParams = lp
            img.scaleType = ImageView.ScaleType.CENTER_CROP
        img.transitionName = "moment_image_${moment.id}_$index"

        val cornerSize = 8f.dpToPx(holder.itemView.context) // 减小圆角
        val builder = img.shapeAppearanceModel.toBuilder()
        
        if (isFirstOfTwo) {
            // 第一张图片：左上和左下有圆角，右上和右下无圆角
            builder.setTopLeftCornerSize(cornerSize)
                .setTopRightCornerSize(0f)
                .setBottomLeftCornerSize(cornerSize)
                .setBottomRightCornerSize(0f)
        } else if (isSecondOfTwo) {
            // 第二张图片：右上和右下有圆角，左上和左下无圆角
            builder.setTopLeftCornerSize(0f)
                .setTopRightCornerSize(cornerSize)
                .setBottomLeftCornerSize(0f)
                .setBottomRightCornerSize(cornerSize)
        } else {
            // 其他情况：所有角都有圆角
            builder.setAllCornerSizes(cornerSize)
        }
        
        img.shapeAppearanceModel = builder.build()
        
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
     * 设置视频显示
     */
    private fun setupVideo(holder: MomentViewHolder, moment: Moment) {
        val context = holder.itemView.context
        val screenWidth = holder.itemView.resources.displayMetrics.widthPixels
        val padding = 68.dpToPx(context) // 左边距
        val margin = 2.dpToPx(context) // 间距
        val availableWidth = screenWidth - padding - 32.dpToPx(context) // 可用宽度
        
        // 创建视频容器
        val videoView = LayoutInflater.from(context)
            .inflate(R.layout.item_moment_video, holder.imageContainer, false)
        
        val playerView = videoView.findViewById<PlayerView>(R.id.playerView)
        val tvVideoDuration = videoView.findViewById<TextView>(R.id.tvVideoDuration)
        
        // 设置视频尺寸（与单张横图相同）
        val videoWidth = availableWidth
        val videoHeight = (videoWidth * 0.75f).toInt() // 4:3 比例
        
        val lp = FlexboxLayout.LayoutParams(videoWidth, videoHeight)
        lp.setMargins(margin, margin, margin, margin)
        videoView.layoutParams = lp
        
        // 设置视频时长
        val durationText = formatVideoDuration(moment.videoDuration)
        tvVideoDuration.text = durationText
        
        // 创建或获取播放器
        val player = getOrCreatePlayer(context, moment)
        playerView.player = player
        
        // 存储播放器引用，用于后续管理
        playerView.tag = moment.id
        
        holder.imageContainer.addView(videoView)
    }
    
    /**
     * 获取或创建视频播放器
     */
    private fun getOrCreatePlayer(context: Context, moment: Moment): ExoPlayer {
        val momentId = moment.id
        val videoUrl = moment.videoUrl ?: return ExoPlayer.Builder(context).build()
        
        return videoPlayers.getOrPut(momentId) {
            ExoPlayer.Builder(context).build().apply {
                // 创建媒体项
                val uri = if (videoUrl.startsWith("http://") || videoUrl.startsWith("https://")) {
                    Uri.parse(videoUrl)
                } else if (videoUrl.startsWith("file:///android_asset/")) {
                    Uri.parse(videoUrl)
                } else {
                    Uri.parse("file:///android_asset/$videoUrl")
                }
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
                // 默认不自动播放，等待滚动到中间时再播放
                playWhenReady = false
            }
        }
    }
    
    /**
     * 播放指定视频
     */
    fun playVideo(momentId: String) {
        // 暂停当前播放的视频
        currentPlayingVideoId?.let { currentId ->
            if (currentId != momentId) {
                videoPlayers[currentId]?.playWhenReady = false
            }
        }
        
        // 播放新视频
        videoPlayers[momentId]?.let { player ->
            player.playWhenReady = true
            currentPlayingVideoId = momentId
        }
    }
    
    /**
     * 暂停指定视频
     */
    fun pauseVideo(momentId: String) {
        videoPlayers[momentId]?.playWhenReady = false
        if (currentPlayingVideoId == momentId) {
            currentPlayingVideoId = null
        }
    }
    
    /**
     * 释放所有播放器
     */
    fun releaseAllPlayers() {
        videoPlayers.values.forEach { player ->
            player.release()
        }
        videoPlayers.clear()
        currentPlayingVideoId = null
    }
    
    /**
     * 释放指定播放器
     */
    fun releasePlayer(momentId: String) {
        videoPlayers[momentId]?.let { player ->
            player.release()
            videoPlayers.remove(momentId)
            if (currentPlayingVideoId == momentId) {
                currentPlayingVideoId = null
            }
        }
    }
    
    /**
     * 格式化视频时长
     */
    private fun formatVideoDuration(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun Float.dpToPx(context: Context): Float {
        return this * context.resources.displayMetrics.density
    }
}