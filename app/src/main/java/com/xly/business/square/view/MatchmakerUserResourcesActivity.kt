package com.xly.business.square.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.jspp.model.UserCard
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.model.Matchmaker
import com.xly.business.recommend.view.HometownFragment
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.business.square.view.adapter.BlurTransformation
import com.xly.databinding.ActivityMatchmakerUserResourcesBinding
import com.xly.middlelibrary.utils.MatchmakerMockData

class MatchmakerUserResourcesActivity : LYBaseActivity<ActivityMatchmakerUserResourcesBinding, RecommendViewModel>() {

    private lateinit var matchmaker: Matchmaker
    private val primaryColor = Color.parseColor("#FF6B6B") // 主题色温暖珊瑚红
    private var statusToolbarBackground: View? = null
    private var lastAppliedColor: Int = Color.TRANSPARENT // 缓存上次应用的颜色，避免不必要的更新

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
        
        // 设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        
        setupToolbar()
        setupStatusToolbarBackground()
        setupMatchmakerInfo()
        setupBlurBackground()
        setupRecyclerView()
        setupScrollListener()
    }

    private fun setupToolbar() {
        viewBind.btnBack.setOnClickListener {
            finish()
        }
        
        // 设置工具栏
        setSupportActionBar(viewBind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // 使用自定义返回按钮
        
        // 获取状态栏和工具栏背景View
        statusToolbarBackground = viewBind.statusToolbarBackground
        statusToolbarBackground?.setBackgroundColor(Color.TRANSPARENT)
    }
    
    private fun setupStatusToolbarBackground() {
        statusToolbarBackground?.let { background ->
            // 等待布局完成后再设置高度和位置
            background.post {
                val statusBarHeight = getStatusBarHeight()
                
                // 获取Toolbar的实际高度
                val toolbarHeightPx = viewBind.toolbar.height.takeIf { it > 0 } ?: run {
                    // 如果Toolbar还没有测量完成，使用actionBarSize的标准值（56dp）
                    val actionBarSizeAttr = intArrayOf(android.R.attr.actionBarSize)
                    val typedArray = obtainStyledAttributes(actionBarSizeAttr)
                    val actionBarSize = typedArray.getDimensionPixelSize(0, 0)
                    typedArray.recycle()
                    actionBarSize
                }
                
                // 为了确保完全覆盖，稍微增加一点高度（增加2dp作为安全边距）
                val extraHeight = (2 * resources.displayMetrics.density).toInt()
                val totalHeight = statusBarHeight + toolbarHeightPx + extraHeight
                
                val layoutParams = background.layoutParams
                layoutParams.height = totalHeight
                
                // 由于占位View在CollapsingToolbarLayout内部，并且CollapsingToolbarLayout有fitsSystemWindows="true"
                // CollapsingToolbarLayout的内容区域从状态栏下方开始
                // 占位View需要向上偏移状态栏高度，才能覆盖状态栏区域
                // 使用负的marginTop让View向上延伸到状态栏区域
                if (layoutParams is android.view.ViewGroup.MarginLayoutParams) {
                    // 确保负的marginTop能够完全覆盖状态栏
                    layoutParams.topMargin = -statusBarHeight
                }
                background.layoutParams = layoutParams
                
                // 确保占位View在Toolbar下方，作为背景层
                // 由于占位View在布局中位于Toolbar之前，它会在Toolbar下方绘制
                // Toolbar的背景是透明的，所以占位View的颜色会显示出来
            }
        }
    }
    
    private fun getStatusBarHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = window.decorView.rootWindowInsets
            windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        } else {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            result
        }
    }
    
    private fun setupScrollListener() {
        // 监听 AppBarLayout 的滚动偏移，实现状态栏和 Toolbar 红色渐变
        viewBind.appBarLayout.addOnOffsetChangedListener { appBar, verticalOffset ->
            val totalScrollRange = appBar.totalScrollRange
            val scrollRatio = if (totalScrollRange != 0) {
                (-verticalOffset).toFloat() / totalScrollRange
            } else {
                0f
            }
            // 限制在 0-1 之间
            val clampedRatio = scrollRatio.coerceIn(0f, 1f)
            // 检查是否完全折叠：verticalOffset 的绝对值等于 totalScrollRange
            val isFullyCollapsed = totalScrollRange != 0 && kotlin.math.abs(verticalOffset) >= totalScrollRange
            updateStatusBarColor(clampedRatio, isFullyCollapsed)
        }
    }
    
    private fun updateStatusBarColor(scrollRatio: Float, isFullyCollapsed: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        
        val background = this.statusToolbarBackground ?: return
        
        // 调整渐变时机，让状态栏和 Toolbar 的渐变完全同步
        // 当滚动比例达到这个阈值时，开始从透明渐变到红色
        val threshold = 0.7f
        
        // 获取红色的 RGB 分量（不包含 alpha）
        val red = Color.red(primaryColor)
        val green = Color.green(primaryColor)
        val blue = Color.blue(primaryColor)
        
        val finalColor: Int
        val currentAlpha: Int
        
        if (isFullyCollapsed || scrollRatio >= 1.0f) {
            // 完全折叠时，使用完全不透明的红色
            finalColor = primaryColor
            currentAlpha = 255
        } else if (scrollRatio < threshold) {
            // 在阈值之前，保持完全透明
            finalColor = Color.TRANSPARENT
            currentAlpha = 0
        } else {
            // 在阈值之后，计算渐变（在 threshold 到 1.0 之间）
            val gradientRatio = ((scrollRatio - threshold) / (1.0f - threshold)).coerceIn(0f, 1f)
            // 使用平滑插值函数，减少颜色突变，使渐变更平滑
            val smoothRatio = gradientRatio * gradientRatio * (3f - 2f * gradientRatio) // smoothstep
            currentAlpha = (smoothRatio * 255).toInt().coerceIn(0, 255)
            
            // 计算最终颜色（带透明度）
            finalColor = Color.argb(currentAlpha, red, green, blue)
        }
        
        // 防抖机制：只在颜色变化超过阈值时才更新，减少频繁更新导致的闪烁
        // 在关键状态（完全透明或完全不透明）时总是更新
        // 在渐变过程中，只在 alpha 值变化超过 8 时才更新（减少更新频率）
        val lastAlpha = Color.alpha(lastAppliedColor)
        val shouldUpdate = when {
            currentAlpha == 0 || currentAlpha == 255 -> {
                // 关键状态：总是更新
                finalColor != lastAppliedColor
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
        lastAppliedColor = finalColor
        
        // 直接设置占位View的背景色，作为状态栏和Toolbar的整体背景
        // 占位View已经覆盖了状态栏和Toolbar区域，所以只需要设置占位View的颜色
        // 状态栏保持透明，让占位View的颜色显示出来，确保状态栏和Toolbar颜色完全一致
        background.setBackgroundColor(finalColor)
        
        // 保持状态栏透明，让占位View的颜色显示出来
        // 如果同时设置window.statusBarColor，会导致颜色叠加，造成状态栏颜色更深
        window.statusBarColor = Color.TRANSPARENT
    }
    
    private fun setupMatchmakerInfo() {
        // 设置红娘头像
        val context = viewBind.root.context
        val resourceId = context.resources.getIdentifier(
            matchmaker.avatar,
            "mipmap",
            context.packageName
        )
        if (resourceId != 0) {
            Glide.with(context)
                .load(resourceId)
                .circleCrop()
                .into(viewBind.ivMatchmakerAvatar)
        } else {
            viewBind.ivMatchmakerAvatar.setImageResource(R.mipmap.head_img)
        }
        
        // 设置红娘名字
        viewBind.tvMatchmakerName.text = matchmaker.name
        
        // 设置红娘位置
        viewBind.tvMatchmakerLocation.text = "📍 ${matchmaker.location}"
        
        // 设置红娘评分
        viewBind.tvMatchmakerRating.text = "⭐ ${String.format("%.1f", matchmaker.rating)}分"
        
        // 设置红娘简介
        viewBind.tvMatchmakerDescription.text = matchmaker.description
        
        // 设置用户数量
        viewBind.tvUserCount.text = "${matchmaker.userCount}位用户"
        
        // 设置标签
        setupMatchmakerTags()
    }
    
    private fun setupMatchmakerTags() {
        viewBind.llMatchmakerTags.removeAllViews()
        if (matchmaker.tags.isNotEmpty()) {
            viewBind.llMatchmakerTags.visibility = View.VISIBLE
            matchmaker.tags.forEach { tag ->
                val tagView = layoutInflater.inflate(R.layout.item_tag, viewBind.llMatchmakerTags, false)
                val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
                tvTag.text = tag
                viewBind.llMatchmakerTags.addView(tagView)
            }
        } else {
            viewBind.llMatchmakerTags.visibility = View.GONE
        }
    }
    
    private fun setupBlurBackground() {
        // 加载红娘头像并模糊处理作为背景
        val context = viewBind.root.context
        val resourceId = context.resources.getIdentifier(
            matchmaker.avatar,
            "mipmap",
            context.packageName
        )
        
        if (resourceId != 0) {
            Glide.with(context)
                .load(resourceId)
                .transform(BlurTransformation(context, 25f))
                .into(viewBind.ivBlurBackground)
        } else {
            // 使用默认头像
            Glide.with(context)
                .load(R.mipmap.head_img)
                .transform(BlurTransformation(context, 25f))
                .into(viewBind.ivBlurBackground)
        }
    }

    private fun setupRecyclerView() {
        // 使用同乡页面的适配器样式
        val hometownAdapter = HometownFragment.HometownAdapter { user, avatarView ->
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
        }

        // 使用网格布局，两列（同乡页面样式）
        viewBind.recyclerView.layoutManager = GridLayoutManager(this, 2)
        viewBind.recyclerView.adapter = hometownAdapter
        
        // 设置 padding（同乡页面样式）
        viewBind.recyclerView.setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        viewBind.recyclerView.clipToPadding = false
        
        // 加载数据并转换为同乡页面的数据格式
        loadUserResourcesForHometown(hometownAdapter)
    }
    
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun loadUserResourcesForHometown(adapter: HometownFragment.HometownAdapter) {
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
