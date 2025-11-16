package com.xly.business.square.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.view.HometownFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.model.Matchmaker
import com.xly.business.square.view.adapter.MatchmakerUserResourcesAdapter
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.ActivityMatchmakerUserResourcesBinding
import com.xly.databinding.ItemMatchmakerHeaderBinding
import com.xly.middlelibrary.utils.MatchmakerMockData
import com.jspp.model.UserCard

class MatchmakerUserResourcesActivity : LYBaseActivity<ActivityMatchmakerUserResourcesBinding, RecommendViewModel>() {

    private lateinit var matchmaker: Matchmaker
    private lateinit var adapter: MatchmakerUserResourcesAdapter
    private var headerBinding: ItemMatchmakerHeaderBinding? = null
    private var isScrolling = false
    private var scrollRunnable: Runnable? = null
    private var contactButton: View? = null
    private var lastTopNaviBgColor: Int = Color.TRANSPARENT
    private val headerHeight = 320 // header 的高度（dp转px后的值）

    companion object {
        const val EXTRA_MATCHMAKER_ID = "matchmaker_id"

        fun start(context: android.content.Context, matchmaker: Matchmaker) {
            val intent = Intent(context, MatchmakerUserResourcesActivity::class.java).apply {
                putExtra(EXTRA_MATCHMAKER_ID, matchmaker.id)
            }
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityMatchmakerUserResourcesBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[RecommendViewModel::class.java]

    override fun acceptData() {
        super.acceptData()
        // 获取传递的红娘ID，然后获取红娘信息
        val matchmakerId = intent.getStringExtra(EXTRA_MATCHMAKER_ID)
            ?: throw IllegalArgumentException("Matchmaker ID is required")
        matchmaker = MatchmakerMockData.generateMatchmakerById(matchmakerId)
            ?: throw IllegalArgumentException("Matchmaker not found: $matchmakerId")
    }

    override fun initView() {
        super.initView()
        setupBackButton()
        setupRecyclerView()
        setupRefreshLayout()
        setupContactButton()
        loadUserResources()
    }

    private fun setupBackButton() {
        viewBind.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        // 创建适配器
        adapter = MatchmakerUserResourcesAdapter(
            matchmaker,
            onUserClick = { user, avatarView ->
                // 点击用户卡片，跳转到用户详情页（带转场动画）
                val intent = Intent(this, LYUserDetailInfoActivity::class.java).apply {
                    putExtra("user_id", user.id)
                    putExtra("user_name", user.name)
                    putExtra("user_avatar", user.avatar)
                }

                if (avatarView != null) {
                    val transitionName = "user_avatar_${user.id}"
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        Pair.create(avatarView, transitionName)
                    )
                    startActivity(intent, options.toBundle())
                } else {
                    startActivity(intent)
                }
            },
            onHeaderCreated = { binding ->
                // 保存 headerBinding 引用
                headerBinding = binding
                // 设置按钮点击事件
                setupHeaderButtons(binding)
            }
        )

        // 使用网格布局，两列
        val gridLayoutManager = GridLayoutManager(this, 2)
        // 设置 spanSizeLookup，让 header 占满整行（2列）
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1 // header 占2列，用户项占1列
            }
        }

        viewBind.recyclerView.layoutManager = gridLayoutManager
        viewBind.recyclerView.adapter = adapter

        // 设置 padding（同乡页面样式）
        viewBind.recyclerView.setPadding(8.dpToPx(), 0, 8.dpToPx(), 8.dpToPx())
        viewBind.recyclerView.clipToPadding = false
        
        // 设置滚动监听
        setupScrollListener()
        
        // 初始化 topNaviBg 颜色
        viewBind.recyclerView.post {
            updateTopNaviBgColor()
        }
    }
    
    private fun setupHeaderButtons(binding: ItemMatchmakerHeaderBinding) {
        // 关注按钮点击事件
        binding.llFollow.setOnClickListener {
            // TODO: 实现关注功能
        }
    }
    
    private fun setupContactButton() {
        contactButton = viewBind.llContact
        // 联系红娘按钮点击事件
        viewBind.llContact.setOnClickListener {
            // TODO: 实现联系红娘功能
        }
    }
    
    private fun setupScrollListener() {
        viewBind.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING,
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 开始滚动，隐藏按钮
                        if (!isScrolling) {
                            isScrolling = true
                            hideButtons()
                        }
                        // 取消之前的延迟任务
                        scrollRunnable?.let { viewBind.recyclerView.removeCallbacks(it) }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 停止滚动，延迟显示按钮（动画）
                        scrollRunnable = Runnable {
                            isScrolling = false
                            showButtons()
                        }
                        viewBind.recyclerView.postDelayed(scrollRunnable!!, 300) // 300ms 后显示
                    }
                }
            }
            
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateTopNaviBgColor()
            }
        })
    }
    
    private fun updateTopNaviBgColor() {
        val recyclerView = viewBind.recyclerView
        val scrollY = recyclerView.computeVerticalScrollOffset()
        
        // header 的高度（px）
        val headerHeightPx = (headerHeight * resources.displayMetrics.density).toInt()
        
        // 计算滚动比例：0 表示在顶部（完全透明），1 表示滚动超过 header 高度（完全不透明）
        val scrollRatio = (scrollY.toFloat() / headerHeightPx).coerceIn(0f, 1f)
        
        // 目标颜色（主题色珊瑚红）
        val targetColor = ContextCompat.getColor(this, R.color.brand_primary)
        val targetRed = Color.red(targetColor)
        val targetGreen = Color.green(targetColor)
        val targetBlue = Color.blue(targetColor)
        
        // 根据滚动方向计算颜色
        val currentAlpha: Int
        val finalColor: Int
        
        if (scrollY <= 0) {
            // 向下滑动或在顶部，完全透明
            currentAlpha = 0
            finalColor = Color.TRANSPARENT
        } else {
            // 向上滑动，从透明渐变到白色
            // 使用平滑插值函数，使渐变更平滑
            val smoothRatio = scrollRatio * scrollRatio * (3f - 2f * scrollRatio) // smoothstep
            currentAlpha = (smoothRatio * 255).toInt().coerceIn(0, 255)
            finalColor = Color.argb(currentAlpha, targetRed, targetGreen, targetBlue)
        }
        
        // 防抖机制：只在颜色变化超过阈值时才更新
        val lastAlpha = Color.alpha(lastTopNaviBgColor)
        val shouldUpdate = when {
            currentAlpha == 0 || currentAlpha == 255 -> {
                // 关键状态：总是更新
                finalColor != lastTopNaviBgColor
            }
            kotlin.math.abs(currentAlpha - lastAlpha) >= 8 -> {
                // 渐变状态：只在变化超过阈值时更新
                true
            }
            else -> {
                // 变化太小，跳过更新
                false
            }
        }
        
        if (!shouldUpdate) {
            return
        }
        
        // 更新缓存
        lastTopNaviBgColor = finalColor
        
        // 更新 topNaviBg 的背景颜色
        viewBind.topNaviBg.setBackgroundColor(finalColor)
    }
    
    private fun hideButtons() {
        // 只隐藏联系红娘按钮，关注按钮不隐藏
        contactButton?.let {
            animateButtonVisibility(it, false)
        }
    }
    
    private fun showButtons() {
        // 只显示联系红娘按钮，关注按钮不隐藏
        contactButton?.let {
            animateButtonVisibility(it, true)
        }
    }
    
    private fun animateButtonVisibility(button: View, show: Boolean) {
        if (show && button.alpha == 1f) return // 已经显示，不需要动画
        if (!show && button.alpha == 0f) return // 已经隐藏，不需要动画
        
        val animatorSet = AnimatorSet()
        
        if (show) {
            button.visibility = View.VISIBLE
            val alphaAnimator = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f)
            val scaleXAnimator = ObjectAnimator.ofFloat(button, "scaleX", 0.8f, 1f)
            val scaleYAnimator = ObjectAnimator.ofFloat(button, "scaleY", 0.8f, 1f)
            animatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator)
            animatorSet.duration = 300
            animatorSet.interpolator = DecelerateInterpolator()
        } else {
            val alphaAnimator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
            val scaleXAnimator = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.8f)
            val scaleYAnimator = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.8f)
            animatorSet.playTogether(alphaAnimator, scaleXAnimator, scaleYAnimator)
            animatorSet.duration = 200
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    button.visibility = View.GONE
                }
            })
        }
        
        animatorSet.start()
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun setupRefreshLayout() {
        // 设置刷新头部
        viewBind.refreshLayout.setRefreshHeader(
            com.scwang.smart.refresh.header.MaterialHeader(this)
        )
        viewBind.refreshLayout.setEnableRefresh(true)
        viewBind.refreshLayout.setEnableLoadMore(false) // 禁用加载更多
        // 禁用过度滚动，避免与状态栏冲突
        viewBind.refreshLayout.setEnableOverScrollDrag(false)
        viewBind.refreshLayout.setEnableOverScrollBounce(false)

        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            // 重新加载用户资源数据
            loadUserResources()
            // 完成刷新
            refreshLayout.finishRefresh(500) // 500ms后完成刷新
        }
    }

    private fun loadUserResources() {
        // 生成Mock用户资源数据并转换为同乡页面的数据格式
        val allMockUsers = generateMockUserResources(matchmaker.id)

        // 转换为同乡页面的数据格式
        val avatarResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )

        val hometownUsers = allMockUsers.mapIndexed { index, userCard ->
            HometownFragment.HometownUser(
                id = userCard.id,
                name = userCard.name,
                age = userCard.age,
                avatar = avatarResources[index % avatarResources.size]
            )
        }

        adapter.submitList(hometownUsers)
    }

    /**
     * 生成Mock用户资源数据
     * TODO: 替换为真实的API调用
     */
    private fun generateMockUserResources(matchmakerId: String): List<UserCard> {
        val names = listOf("张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴", "徐", "孙", "马", "朱", "胡", "林", "郭", "何", "高", "罗")
        val locations = listOf("北京", "上海", "广州", "深圳", "杭州", "成都", "南京", "武汉", "西安", "重庆")
        val occupations = listOf("设计师", "教师", "经理", "编辑", "医生", "律师", "工程师", "会计师", "市场专员", "产品经理", "运营", "HR", "销售", "咨询师", "翻译")
        val educations = listOf("本科", "硕士", "MBA", "博士", "专科")
        val tagsList = listOf(
            listOf("温柔", "旅行", "阅读"),
            listOf("活泼", "运动", "美食"),
            listOf("独立", "事业", "旅行"),
            listOf("文艺", "音乐", "电影"),
            listOf("时尚", "购物", "美容"),
            listOf("健身", "瑜伽", "跑步"),
            listOf("烹饪", "烘焙", "美食"),
            listOf("摄影", "旅行", "户外"),
            listOf("阅读", "写作", "文学"),
            listOf("音乐", "舞蹈", "艺术"),
            listOf("宠物", "动物", "爱心"),
            listOf("科技", "互联网", "创新"),
            listOf("投资", "理财", "金融"),
            listOf("教育", "学习", "成长"),
            listOf("环保", "公益", "志愿者")
        )
        val bios = listOf(
            "温柔善良，喜欢旅行和阅读",
            "活泼开朗，热爱生活",
            "独立自主，事业有成",
            "文艺青年，喜欢音乐和电影",
            "时尚达人，追求品质生活",
            "热爱运动，健康生活",
            "厨艺精湛，喜欢分享美食",
            "摄影爱好者，记录美好瞬间",
            "书虫一枚，喜欢安静阅读",
            "音乐爱好者，会弹钢琴",
            "宠物爱好者，有两只猫咪",
            "科技控，关注最新科技动态",
            "理财达人，善于规划未来",
            "教育工作者，热爱教育事业",
            "环保主义者，关注可持续发展",
            "温柔体贴，善解人意",
            "乐观向上，充满正能量",
            "细心周到，注重细节",
            "幽默风趣，善于沟通",
            "成熟稳重，值得信赖"
        )

        return (1..50).map { index ->
            val nameIndex = index % names.size
            val locationIndex = index % locations.size
            val occupationIndex = index % occupations.size
            val educationIndex = index % educations.size
            val tagsIndex = index % tagsList.size
            val bioIndex = index % bios.size

            UserCard(
                id = "user_${String.format("%03d", index)}",
                name = "${names[nameIndex]}小姐",
                age = 25 + (index % 10), // 25-34岁
                location = locations[locationIndex],
                avatarUrl = "https://example.com/avatar/user${String.format("%03d", index)}.jpg",
                bio = bios[bioIndex],
                tags = tagsList[tagsIndex],
                photos = emptyList(),
                occupation = occupations[occupationIndex],
                education = educations[educationIndex],
                height = 158 + (index % 12), // 158-169cm
                weight = 45 + (index % 10), // 45-54kg
                isOnline = index % 3 != 0, // 约2/3在线
                distance = "${5 + (index % 15)}km", // 5-19km
                lastActiveTime = System.currentTimeMillis() - (index * 3600000L) // 不同时间
            )
        }
    }
}
