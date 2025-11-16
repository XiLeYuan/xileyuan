package com.xly.business.square.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
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
import com.xly.databinding.ActivityMatchmakerUserResourcesBinding
import com.xly.middlelibrary.utils.MatchmakerMockData

class MatchmakerUserResourcesActivity : LYBaseActivity<ActivityMatchmakerUserResourcesBinding, RecommendViewModel>() {

    private lateinit var matchmaker: Matchmaker
    
    private var isScrolling = false
    private val scrollHandler = android.os.Handler(android.os.Looper.getMainLooper())

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
        setupToolbar()
        setupStatusBarPlaceholder()
        setupRecyclerView()
        setupRefreshLayout()
        setupFloatingButton()
    }

    private fun setupToolbar() {
        viewBind.btnBack.setOnClickListener {
            finish()
        }
        
        // 设置导航栏标题为红娘名字
        val toolbarTitle = viewBind.root.findViewById<android.widget.TextView>(R.id.toolbarTitle)
        toolbarTitle?.text = matchmaker.name
    }
    
    private fun setupStatusBarPlaceholder() {
        // 获取状态栏高度并设置占位View的高度
        viewBind.toolbarContainer.post {
            val statusBarHeight = getStatusBarHeight()
            val layoutParams = viewBind.statusBarPlaceholder.layoutParams
            layoutParams.height = statusBarHeight
            viewBind.statusBarPlaceholder.layoutParams = layoutParams
            
            // 设置RecyclerView的paddingTop，避免内容被导航栏遮挡
            // 导航栏高度 = 状态栏高度 + 工具栏高度(56dp)
            val toolbarHeight = viewBind.toolbarContainer.height
            val paddingTop = toolbarHeight + 8.dpToPx() // 导航栏高度 + 间距
            
            viewBind.recyclerView.setPadding(
                viewBind.recyclerView.paddingLeft,
                paddingTop,
                viewBind.recyclerView.paddingRight,
                viewBind.recyclerView.paddingBottom
            )
        }
    }
    
    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
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
        
        // 监听滚动状态，控制悬浮按钮显示/隐藏
        setupScrollListener()
        
        // 加载数据并转换为同乡页面的数据格式
        loadUserResourcesForHometown(hometownAdapter)
    }
    
    private fun setupScrollListener() {
        viewBind.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                
                when (newState) {
                    androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING,
                    androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 开始滚动，隐藏按钮
                        if (!isScrolling) {
                            isScrolling = true
                            hideFloatingButton()
                        }
                        // 移除之前的延迟任务
                        scrollHandler.removeCallbacksAndMessages(null)
                    }
                    androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE -> {
                        // 停止滚动，延迟显示按钮
                        scrollHandler.removeCallbacksAndMessages(null)
                        scrollHandler.postDelayed({
                            isScrolling = false
                            showFloatingButton()
                        }, 300) // 停止滚动300ms后显示按钮
                    }
                }
            }
        })
    }
    
    private fun setupFloatingButton() {
        // 初始状态：按钮可见
        viewBind.fabContactMatchmaker.visibility = android.view.View.VISIBLE
        viewBind.fabContactMatchmaker.alpha = 1f
        viewBind.fabContactMatchmaker.scaleX = 1f
        viewBind.fabContactMatchmaker.scaleY = 1f
        
        viewBind.fabContactMatchmaker.setOnClickListener {
            // TODO: 实现联系红娘功能
            // 可以跳转到聊天页面或拨打电话
            android.widget.Toast.makeText(this, "联系红娘：${matchmaker.name}", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // 页面加载后延迟显示闪动动画
        viewBind.fabContactMatchmaker.postDelayed({
            animateButtonPulse()
        }, 500) // 延迟500ms后执行闪动动画
    }
    
    private fun showFloatingButton() {
        if (viewBind.fabContactMatchmaker.visibility == android.view.View.VISIBLE && 
            viewBind.fabContactMatchmaker.alpha == 1f) {
            return // 已经显示，不需要动画
        }
        
        viewBind.fabContactMatchmaker.visibility = android.view.View.VISIBLE
        viewBind.fabContactMatchmaker.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(200)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .withEndAction {
                // 显示后添加闪动动画
                animateButtonPulse()
            }
            .start()
    }
    
    private fun animateButtonPulse() {
        // 闪动动画：快速缩放两次
        val animatorSet = android.animation.AnimatorSet()
        
        val scaleUp1 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleX",
            1f, 1.15f
        ).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleUpY1 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleY",
            1f, 1.15f
        ).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleDown1 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleX",
            1.15f, 1f
        ).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleDownY1 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleY",
            1.15f, 1f
        ).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleUp2 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleX",
            1f, 1.1f
        ).apply {
            duration = 100
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleUpY2 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleY",
            1f, 1.1f
        ).apply {
            duration = 100
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleDown2 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleX",
            1.1f, 1f
        ).apply {
            duration = 100
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        val scaleDownY2 = android.animation.ObjectAnimator.ofFloat(
            viewBind.fabContactMatchmaker,
            "scaleY",
            1.1f, 1f
        ).apply {
            duration = 100
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        
        // 第一次闪动
        animatorSet.playTogether(scaleUp1, scaleUpY1)
        animatorSet.play(scaleDown1).after(scaleUp1)
        animatorSet.play(scaleDownY1).after(scaleUpY1)
        
        // 第二次闪动（稍小）
        animatorSet.play(scaleUp2).after(scaleDown1)
        animatorSet.play(scaleUpY2).after(scaleDownY1)
        animatorSet.play(scaleDown2).after(scaleUp2)
        animatorSet.play(scaleDownY2).after(scaleUpY2)
        
        animatorSet.start()
    }
    
    private fun hideFloatingButton() {
        if (viewBind.fabContactMatchmaker.visibility == android.view.View.INVISIBLE) {
            return // 已经隐藏，不需要动画
        }
        
        viewBind.fabContactMatchmaker.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .translationY(viewBind.fabContactMatchmaker.height.toFloat())
            .setDuration(200)
            .setInterpolator(android.view.animation.AccelerateInterpolator())
            .withEndAction {
                viewBind.fabContactMatchmaker.visibility = android.view.View.INVISIBLE
            }
            .start()
    }
    
    private fun setupRefreshLayout() {
        // 设置刷新头部和加载更多底部
        viewBind.refreshLayout.setRefreshHeader(
            com.scwang.smart.refresh.header.MaterialHeader(this)
        )
        viewBind.refreshLayout.setRefreshFooter(
            com.scwang.smart.refresh.footer.ClassicsFooter(this)
        )
        
        // 确保刷新功能启用
        viewBind.refreshLayout.setEnableRefresh(true)
        
        // 下拉刷新
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            // 重新加载数据
            val adapter = viewBind.recyclerView.adapter as? HometownFragment.HometownAdapter
            adapter?.let {
                loadUserResourcesForHometown(it)
            }
            refreshLayout.finishRefresh()
        }
        
        // 加载更多（暂时禁用，同乡页面样式不需要分页）
        viewBind.refreshLayout.setEnableLoadMore(false)
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
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理 Handler，避免内存泄漏
        scrollHandler.removeCallbacksAndMessages(null)
    }
}

