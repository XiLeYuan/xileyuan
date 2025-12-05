package com.xly.business.square.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.base.LYBaseFragment
import com.xly.business.square.model.Moment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentFindBinding
import com.xly.R
import com.xly.business.square.view.adapter.MomentAdapter
import com.xly.business.square.view.adapter.BannerItem

class MomentFragment : LYBaseFragment<FragmentFindBinding,RecommendViewModel>() {

    private var adapter: MomentAdapter? = null
    private var currentPage = 1
    private val pageSize = 10
    private var hasMoreData = true
    private var isLoading = false
    
    // 浮动按钮相关
    private var isFloatButtonVisible = true
    private var isExpanded = false
    private var scrollHandler: Handler? = Handler()
    private val scrollRunnable = Runnable {
        // 滚动停止后显示按钮
        showFloatButton()
    }
    
    // Mock banner 数据
    private val bannerList = listOf(
        BannerItem(
            id = "1",
            imageResId = R.mipmap.active1,
            title = "春季婚博会活动",
            url = null
        ),
        BannerItem(
            id = "2",
            imageResId = R.mipmap.active2,
            title = "限时优惠活动",
            url = null
        ),
        BannerItem(
            id = "3",
            imageResId = R.mipmap.activie3,
            title = "新人专享福利",
            url = null
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRefreshLayout()
        setupRecyclerView()
        setupFloatButton()
        
        // 初始加载数据
        loadData(isRefresh = false)
    }
    
    /**
     * 获取图片资源ID的辅助函数
     */
    private fun getImageResId(name: String): Int {
        return resources.getIdentifier(name, "mipmap", requireContext().packageName)
    }
    
    /**
     * 生成Mock数据
     */
    private fun generateMockData(page: Int, pageSize: Int): List<Moment> {
        val imageResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )
        val imageResIds = imageResources.map { getImageResId(it) }
        
        // 用户名称列表
        val userNames = listOf(
            "小美", "小红", "小丽", "小芳", "小雅", "小静", "小雯", "小云",
            "小月", "小星", "小晴", "小欣", "小琳", "小悦", "小瑶", "小琪",
            "小涵", "小萱", "小薇", "小蕊", "小梦", "小语", "小诗", "小画"
        )
        
        // 内容模板
        val contentTemplates = listOf(
            "今天天气真好，出去走走～",
            "周末和朋友一起聚餐，很开心！",
            "今天去公园拍照，风景很美",
            "和闺蜜一起逛街，买了好多东西",
            "今天做了好多好吃的，分享给大家",
            "周末旅行，拍了很多美照",
            "今天拍了好多照片，每一张都很喜欢",
            "周末旅行，拍了很多美照，分享给大家",
            "今天心情不错，分享一些日常",
            "和朋友们一起度过愉快的周末",
            "今天去看了电影，非常精彩",
            "竖图的效果也很不错呢",
            "今天拍了一张竖图，感觉很不错",
            "生活就是要记录美好瞬间",
            "今天的夕阳特别美",
            "和TA一起看日出，太浪漫了",
            "周末的下午茶时光",
            "记录生活中的小确幸",
            "今天尝试了新的拍照角度",
            "分享一张很满意的照片",
            "生活中的美好值得被记录",
            "今天的心情特别好",
            "和朋友们一起的快乐时光",
            "记录每一个值得纪念的瞬间"
        )
        
        // 时间列表
        val timeList = listOf(
            "刚刚", "1分钟前", "2分钟前", "5分钟前", "10分钟前", "15分钟前",
            "20分钟前", "30分钟前", "1小时前", "2小时前", "3小时前", "4小时前",
            "5小时前", "6小时前", "昨天", "2天前", "3天前", "一周前"
        )
        
        // 图片数量配置（1-8张，循环使用）
        val imageCountConfigs = listOf(1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 2, 1, 3, 4, 5)
        
        val startIndex = (page - 1) * pageSize
        val endIndex = startIndex + pageSize
        val totalMockItems = 100 // 总共100条mock数据
        
        // 如果超过总数，返回空列表
        if (startIndex >= totalMockItems) {
            return emptyList()
        }
        
        val actualEndIndex = minOf(endIndex, totalMockItems)
        val moments = mutableListOf<Moment>()
        
        for (i in startIndex until actualEndIndex) {
            val userNameIndex = i % userNames.size
            val contentIndex = i % contentTemplates.size
            val timeIndex = i % timeList.size
            val imageCountIndex = i % imageCountConfigs.size
            val avatarIndex = i % imageResources.size
            
            val imageCount = imageCountConfigs[imageCountIndex]
            
            // 每10条数据中有1条是视频（约10%的视频比例）
            val isVideo = i % 10 == 0
            
            if (isVideo) {
                // 视频动态
                val videoThumbnailIndex = i % imageResIds.size
                val videoDuration = 30L + (i % 120) // 30-150秒随机时长
                
                // 本地视频文件列表（放在 assets/videos/ 文件夹下）
                val localVideos = listOf(
                    "videos/demo.mp4"
                )
                // 使用本地视频
                val videoUrl = localVideos[0] // 使用 demo.mp4
                
                moments.add(
                    Moment(
                        id = "moment_${i + 1}",
                        userAvatar = getImageResId(imageResources[avatarIndex]),
                        userName = userNames[userNameIndex],
                        content = contentTemplates[contentIndex],
                        images = listOf(imageResIds[videoThumbnailIndex]), // 视频封面
                        time = timeList[timeIndex],
                        isVertical = false,
                        videoUrl = videoUrl,
                        videoThumbnail = imageResIds[videoThumbnailIndex],
                        videoDuration = videoDuration
                    )
                )
            } else {
                // 图片动态
                // 优化图片选择：让图片选择更加随机和多样化
                // 使用索引和图片数量的组合来生成不同的起始位置
                val startImageIndex = (i * 3 + imageCount * 7) % imageResIds.size
                val images = when {
                    imageCount == 1 -> {
                        // 单张图片：使用不同的图片索引
                        listOf(imageResIds[startImageIndex])
                    }
                    imageCount == 2 -> {
                        // 两张图片：选择不相邻的图片，增加多样性
                        val secondIndex = (startImageIndex + 3) % imageResIds.size
                        listOf(imageResIds[startImageIndex], imageResIds[secondIndex])
                    }
                    imageCount == 3 -> {
                        // 三张图片：选择分散的图片
                        val secondIndex = (startImageIndex + 2) % imageResIds.size
                        val thirdIndex = (startImageIndex + 5) % imageResIds.size
                        listOf(imageResIds[startImageIndex], imageResIds[secondIndex], imageResIds[thirdIndex])
                    }
                    else -> {
                        // 多张图片：从起始位置开始取，但跳过一些图片增加多样性
                        val selectedImages = mutableListOf<Int>()
                        var currentIndex = startImageIndex
                        for (j in 0 until imageCount) {
                            selectedImages.add(imageResIds[currentIndex])
                            // 每次跳过1-2张图片，避免连续选择
                            currentIndex = (currentIndex + (if (j % 2 == 0) 2 else 1)) % imageResIds.size
                        }
                        selectedImages
                    }
                }
                
                val isVertical = imageCount == 1 && i % 3 == 0 // 每3条单张图片中有一条是竖图
                
                moments.add(
                    Moment(
                        id = "moment_${i + 1}",
                        userAvatar = getImageResId(imageResources[avatarIndex]),
                        userName = userNames[userNameIndex],
                        content = contentTemplates[contentIndex],
                        images = images,
                        time = timeList[timeIndex],
                        isVertical = isVertical
                    )
                )
            }
        }
        
        return moments
    }
    
    /**
     * 设置刷新布局
     */
    private fun setupRefreshLayout() {
        viewBind.refreshLayout.setRefreshHeader(MaterialHeader(requireActivity()))
        viewBind.refreshLayout.setRefreshFooter(ClassicsFooter(requireActivity()))
        viewBind.refreshLayout.setEnableRefresh(true)
        viewBind.refreshLayout.setEnableLoadMore(true)

        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            if (!isLoading) {
                loadData(isRefresh = true) {
                    refreshLayout.finishRefresh()
                }
            } else {
                refreshLayout.finishRefresh()
            }
        }

        // 上拉加载更多监听
        viewBind.refreshLayout.setOnLoadMoreListener { refreshLayout ->
            if (!isLoading && hasMoreData) {
                loadData(isRefresh = false) {
                    refreshLayout.finishLoadMore(hasMoreData)
                }
            } else {
                refreshLayout.finishLoadMore(!hasMoreData)
            }
        }
    }
    
    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = MomentAdapter(
            mutableListOf(),
            requireActivity(),
            bannerList = bannerList,
            onBannerClick = { banner ->
                // Banner 点击事件处理
                // TODO: 跳转到活动详情页面
            }
        )

        viewBind.momentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBind.momentRecyclerView.adapter = adapter
        
        // 添加滚动监听，实现视频自动播放
        setupVideoAutoPlay()
    }
    
    /**
     * 设置视频自动播放
     */
    private fun setupVideoAutoPlay() {
        viewBind.momentRecyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkAndPlayVideo(recyclerView)
                
                // 滚动时隐藏浮动按钮
                if (dy != 0) {
                    hideFloatButton()
                    // 移除之前的runnable，重新计时
                    scrollHandler?.removeCallbacks(scrollRunnable)
                    scrollHandler?.postDelayed(scrollRunnable, 500) // 500ms后显示
                }
            }
            
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
                    // 滚动停止时检查并播放视频
                    checkAndPlayVideo(recyclerView)
                    // 滚动停止后显示浮动按钮
                    scrollHandler?.removeCallbacks(scrollRunnable)
                    scrollHandler?.postDelayed(scrollRunnable, 300) // 300ms后显示
                } else {
                    // 开始滚动时隐藏
                    hideFloatButton()
                }
            }
        })
    }
    
    /**
     * 检查并播放屏幕中间的视频
     */
    private fun checkAndPlayVideo(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        val layoutManager = recyclerView.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager ?: return
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        
        // 获取屏幕中间区域（使用屏幕高度，而不是RecyclerView高度）
        val screenHeight = resources.displayMetrics.heightPixels
        val centerAreaTop = screenHeight * 0.3f  // 屏幕上方30%以下
        val centerAreaBottom = screenHeight * 0.7f  // 屏幕下方70%以上
        
        // 遍历所有可见的item，找到在屏幕中间区域的视频
        var centerVideoItem: Moment? = null
        
        for (i in firstVisible..lastVisible) {
            val item = adapter?.getDataList()?.getOrNull(i)
            if (item != null && !item.videoUrl.isNullOrEmpty()) {
                val itemView = layoutManager.findViewByPosition(i)
                if (itemView != null) {
                    // 找到PlayerView（视频实际显示的位置）
                    val playerView = findPlayerView(itemView)
                    if (playerView != null) {
                        // 检查PlayerView是否在屏幕中间区域
                        if (isViewInCenterArea(playerView, centerAreaTop, centerAreaBottom)) {
                            centerVideoItem = item
                            break // 找到第一个在中间区域的视频就停止
                        }
                    }
                }
            }
        }
        
        // 如果没有找到在中间区域的视频，尝试播放第一个可见的视频（fallback）
        if (centerVideoItem == null) {
            for (i in firstVisible..lastVisible) {
                val item = adapter?.getDataList()?.getOrNull(i)
                if (item != null && !item.videoUrl.isNullOrEmpty()) {
                    centerVideoItem = item
                    break
                }
            }
        }
        
        // 播放中间区域的视频，暂停其他视频
        adapter?.getDataList()?.forEach { moment ->
            if (!moment.videoUrl.isNullOrEmpty()) {
                if (moment.id == centerVideoItem?.id) {
                    adapter?.playVideo(moment.id)
                } else {
                    adapter?.pauseVideo(moment.id)
                }
            }
        }
    }
    
    /**
     * 在itemView中查找PlayerView
     */
    private fun findPlayerView(itemView: View): android.view.View? {
        // PlayerView在imageContainer中
        val imageContainer = itemView.findViewById<com.google.android.flexbox.FlexboxLayout>(R.id.imageContainer)
        if (imageContainer != null) {
            // 遍历imageContainer的子View，找到PlayerView
            for (i in 0 until imageContainer.childCount) {
                val child = imageContainer.getChildAt(i)
                // PlayerView在item_moment_video布局的根FrameLayout中
                if (child is android.widget.FrameLayout) {
                    // 递归查找PlayerView（可能被CardView包裹）
                    val playerView = findPlayerViewRecursive(child)
                    if (playerView != null) {
                        return playerView
                    }
                }
            }
        }
        return null
    }
    
    /**
     * 递归查找PlayerView
     */
    private fun findPlayerViewRecursive(view: android.view.View): com.google.android.exoplayer2.ui.PlayerView? {
        if (view is com.google.android.exoplayer2.ui.PlayerView) {
            return view
        }
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                val result = findPlayerViewRecursive(child)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }
    
    /**
     * 检查View是否在屏幕中间区域
     */
    private fun isViewInCenterArea(view: android.view.View, centerAreaTop: Float, centerAreaBottom: Float): Boolean {
        // 获取View在屏幕中的位置
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val viewTopOnScreen = viewLocation[1]
        val viewBottomOnScreen = viewTopOnScreen + view.height
        
        // 检查视频View的中心点是否在中间区域
        val viewCenter = (viewTopOnScreen + viewBottomOnScreen) / 2f
        return viewCenter >= centerAreaTop && viewCenter <= centerAreaBottom
    }
    
    /**
     * 加载数据
     */
    private fun loadData(isRefresh: Boolean, onComplete: (() -> Unit)? = null) {
        if (isLoading) {
            onComplete?.invoke()
            return
        }
        
        isLoading = true
        
        // 模拟网络请求延迟
        Handler().postDelayed({
            val page = if (isRefresh) {
                currentPage = 1
                hasMoreData = true
                1
            } else {
                currentPage++
                currentPage
            }
            
            val newData = generateMockData(page, pageSize)
            
            if (newData.isEmpty()) {
                hasMoreData = false
            } else {
                // 检查是否还有更多数据
                val totalLoaded = page * pageSize
                hasMoreData = totalLoaded < 100 // 总共100条mock数据
            }
            
            if (isRefresh) {
                // 刷新：替换所有数据
                adapter?.updateData(newData)
            } else {
                // 加载更多：追加数据
                adapter?.addData(newData)
            }
            
            // 数据更新后，检查并播放屏幕中间的视频
            viewBind.momentRecyclerView.post {
                checkAndPlayVideo(viewBind.momentRecyclerView)
            }
            
            isLoading = false
            onComplete?.invoke()
        }, 1500) // 模拟1.5秒网络延迟
    }


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
            }
        })
    }


    override fun initView() {


    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindBinding {
        return FragmentFindBinding.inflate(layoutInflater)
    }



    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
    
    /**
     * 设置浮动按钮
     */
    private fun setupFloatButton() {
        // 初始状态：按钮在屏幕外（右侧），等待布局完成后再设置
        viewBind.floatButtonContainer.post {
            val screenWidth = resources.displayMetrics.widthPixels
            viewBind.floatButtonContainer.translationX = screenWidth.toFloat()
            
            // 延迟显示按钮（页面加载完成后）
            viewBind.floatButtonContainer.postDelayed({
                showFloatButton()
            }, 500)
        }
        
        // 点击圆形按钮
        viewBind.floatButton.setOnClickListener {
            if (!isExpanded) {
                expandFloatButton()
            } else {
                collapseFloatButton()
            }
        }
        
        // 点击展开后的容器
        viewBind.expandedContainer.setOnClickListener {
            // TODO: 跳转到发动态页面
            collapseFloatButton()
        }
    }
    
    /**
     * 显示浮动按钮（从右侧滑入）
     */
    private fun showFloatButton() {
        if (!isFloatButtonVisible) {
            isFloatButtonVisible = true
            val screenWidth = resources.displayMetrics.widthPixels
            val animator = ObjectAnimator.ofFloat(
                viewBind.floatButtonContainer,
                "translationX",
                screenWidth.toFloat(),
                0f
            ).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
            }
            animator.start()
        }
    }
    
    /**
     * 隐藏浮动按钮（滑出到右侧）
     */
    private fun hideFloatButton() {
        if (isFloatButtonVisible) {
            isFloatButtonVisible = false
            // 如果展开状态，先收起
            if (isExpanded) {
                collapseFloatButton()
            }
            val screenWidth = resources.displayMetrics.widthPixels
            val animator = ObjectAnimator.ofFloat(
                viewBind.floatButtonContainer,
                "translationX",
                0f,
                screenWidth.toFloat()
            ).apply {
                duration = 250
                interpolator = AccelerateDecelerateInterpolator()
            }
            animator.start()
        }
    }
    
    /**
     * 展开浮动按钮（横向展开）
     */
    private fun expandFloatButton() {
        if (isExpanded) return
        
        isExpanded = true
        
        // 隐藏圆形按钮
        viewBind.floatButton.visibility = View.GONE
        
        // 显示展开容器
        viewBind.expandedContainer.visibility = View.VISIBLE
        
        // 测量展开容器的目标宽度
        viewBind.expandedContainer.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(56, View.MeasureSpec.EXACTLY)
        )
        val targetWidth = viewBind.expandedContainer.measuredWidth
        
        // 设置初始宽度为0
        val layoutParams = viewBind.expandedContainer.layoutParams
        layoutParams.width = 0
        viewBind.expandedContainer.layoutParams = layoutParams
        viewBind.expandedContainer.requestLayout()
        
        // 横向展开动画
        viewBind.expandedContainer.post {
            val animator = android.animation.ValueAnimator.ofInt(0, targetWidth).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    val width = animation.animatedValue as Int
                    val params = viewBind.expandedContainer.layoutParams
                    params.width = width
                    viewBind.expandedContainer.layoutParams = params
                }
            }
            animator.start()
        }
    }
    
    /**
     * 收起浮动按钮
     */
    private fun collapseFloatButton() {
        if (!isExpanded) return
        
        isExpanded = false
        
        val currentWidth = viewBind.expandedContainer.width
        if (currentWidth <= 0) {
            // 如果宽度已经是0，直接切换视图
            viewBind.floatButton.visibility = View.VISIBLE
            viewBind.expandedContainer.visibility = View.GONE
            return
        }
        
        // 横向收起动画
        val animator = android.animation.ValueAnimator.ofInt(currentWidth, 0).apply {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val width = animation.animatedValue as Int
                val params = viewBind.expandedContainer.layoutParams
                params.width = width
                viewBind.expandedContainer.layoutParams = params
            }
        }
        
        animator.start()
        
        // 动画结束后显示圆形按钮，隐藏展开容器
        viewBind.expandedContainer.postDelayed({
            viewBind.floatButton.visibility = View.VISIBLE
            viewBind.expandedContainer.visibility = View.GONE
            // 重置宽度
            val params = viewBind.expandedContainer.layoutParams
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            viewBind.expandedContainer.layoutParams = params
        }, 250)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // 释放所有视频播放器
        adapter?.releaseAllPlayers()
        // 清理Handler
        scrollHandler?.removeCallbacks(scrollRunnable)
        scrollHandler = null
    }
    
    override fun onPause() {
        super.onPause()
        // Fragment 暂停时暂停所有视频
        adapter?.let { adapter ->
            adapter.getDataList().forEach { moment ->
                if (!moment.videoUrl.isNullOrEmpty()) {
                    adapter.pauseVideo(moment.id)
                }
            }
        }
    }
}