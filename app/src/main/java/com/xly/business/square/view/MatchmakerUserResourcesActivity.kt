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
import com.xly.business.square.view.adapter.MatchmakerUserAdapter
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.ActivityMatchmakerUserResourcesBinding
import com.xly.middlelibrary.utils.MatchmakerMockData

class MatchmakerUserResourcesActivity : LYBaseActivity<ActivityMatchmakerUserResourcesBinding, RecommendViewModel>() {

    private lateinit var matchmaker: Matchmaker
    private lateinit var userAdapter: MatchmakerUserAdapter

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
        setupMatchmakerInfo()
        setupRecyclerView()
        loadUserResources()
    }

    private fun setupToolbar() {
        viewBind.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun setupStatusBarPlaceholder() {
        // 获取状态栏高度并设置占位View的高度
        viewBind.toolbarContainer.post {
            val statusBarHeight = getStatusBarHeight()
            val layoutParams = viewBind.statusBarPlaceholder.layoutParams
            layoutParams.height = statusBarHeight
            viewBind.statusBarPlaceholder.layoutParams = layoutParams
            
            // 设置滚动视图的paddingTop，避免内容被导航栏遮挡
            val toolbarHeight = viewBind.toolbarContainer.height
            viewBind.scrollContent.setPadding(
                viewBind.scrollContent.paddingLeft,
                toolbarHeight,
                viewBind.scrollContent.paddingRight,
                viewBind.scrollContent.paddingBottom
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

    private fun setupMatchmakerInfo() {
        // 红娘头像
        Glide.with(this)
            .load(matchmaker.avatar)
            .placeholder(R.mipmap.head_img)
            .circleCrop()
            .into(viewBind.ivMatchmakerAvatar)

        // 红娘姓名
        viewBind.tvMatchmakerName.text = matchmaker.name

        // 认证标识
        viewBind.ivVerified.visibility = 
            if (matchmaker.isVerified) android.view.View.VISIBLE else android.view.View.GONE

        // VIP标识
        viewBind.tvVIP.visibility = 
            if (matchmaker.isVIP) android.view.View.VISIBLE else android.view.View.GONE

        // 评分
        viewBind.tvRating.text = String.format("%.1f", matchmaker.rating)

        // 用户数量
        viewBind.tvUserCount.text = "${matchmaker.userCount}位用户"

        // 服务区域
        viewBind.tvLocation.text = "📍 ${matchmaker.location}"

        // 简介
        viewBind.tvDescription.text = matchmaker.description

        // 成功率
        viewBind.tvSuccessRate.text = "成功率：${matchmaker.successRate.toInt()}%"

        // 从业年限
        if (matchmaker.yearsOfExperience > 0) {
            viewBind.tvExperience.text = "${matchmaker.yearsOfExperience}年从业经验"
            viewBind.tvExperience.visibility = android.view.View.VISIBLE
        } else {
            viewBind.tvExperience.visibility = android.view.View.GONE
        }

        // 标签
        setupTags(matchmaker.tags)
    }

    private fun setupTags(tags: List<String>) {
        viewBind.llTags.removeAllViews()
        tags.take(3).forEach { tag ->
            val tagView = LayoutInflater.from(this)
                .inflate(R.layout.item_tag, viewBind.llTags, false)
            val tvTag = tagView.findViewById<android.widget.TextView>(R.id.tvTag)
            tvTag.text = tag
            viewBind.llTags.addView(tagView)
        }
    }

    private fun setupRecyclerView() {
        userAdapter = MatchmakerUserAdapter { userCard ->
            // 点击用户卡片，跳转到用户详情页
            val intent = Intent(this, LYUserDetailInfoActivity::class.java).apply {
                putExtra("user_id", userCard.id)
            }
            startActivity(intent)
        }

        // 使用网格布局，每行2个
        viewBind.recyclerView.layoutManager = GridLayoutManager(this, 2)
        viewBind.recyclerView.adapter = userAdapter
        
        // 添加间距
        val spacing = 8.dpToPx()
        viewBind.recyclerView.addItemDecoration(
            object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: android.view.View,
                    parent: androidx.recyclerview.widget.RecyclerView,
                    state: androidx.recyclerview.widget.RecyclerView.State
                ) {
                    outRect.left = spacing / 2
                    outRect.right = spacing / 2
                    outRect.top = spacing / 2
                    outRect.bottom = spacing / 2
                }
            }
        )
    }
    
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun loadUserResources() {
        // TODO: 从ViewModel或API加载该红娘的用户资源
        // 这里先用Mock数据
        val mockUsers = generateMockUserResources(matchmaker.id)
        userAdapter.submitList(mockUsers)
        
        // 更新总数
        viewBind.tvTotalCount.text = "共${mockUsers.size}位"
    }

    /**
     * 生成Mock用户资源数据
     * TODO: 替换为真实的API调用
     */
    private fun generateMockUserResources(matchmakerId: String): List<UserCard> {
        return listOf(
            UserCard(
                id = "user_001",
                name = "张小姐",
                age = 28,
                location = "北京",
                avatarUrl = "https://example.com/avatar/user001.jpg",
                bio = "温柔善良，喜欢旅行和阅读",
                tags = listOf("温柔", "旅行", "阅读"),
                photos = emptyList(),
                occupation = "设计师",
                education = "本科",
                height = 165,
                weight = 50,
                isOnline = true,
                distance = "5km",
                lastActiveTime = System.currentTimeMillis()
            ),
            UserCard(
                id = "user_002",
                name = "李小姐",
                age = 26,
                location = "北京",
                avatarUrl = "https://example.com/avatar/user002.jpg",
                bio = "活泼开朗，热爱生活",
                tags = listOf("活泼", "运动", "美食"),
                photos = emptyList(),
                occupation = "教师",
                education = "硕士",
                height = 162,
                weight = 48,
                isOnline = false,
                distance = "8km",
                lastActiveTime = System.currentTimeMillis() - 3600000
            ),
            UserCard(
                id = "user_003",
                name = "王小姐",
                age = 30,
                location = "北京",
                avatarUrl = "https://example.com/avatar/user003.jpg",
                bio = "独立自主，事业有成",
                tags = listOf("独立", "事业", "旅行"),
                photos = emptyList(),
                occupation = "经理",
                education = "MBA",
                height = 168,
                weight = 52,
                isOnline = true,
                distance = "3km",
                lastActiveTime = System.currentTimeMillis()
            ),
            UserCard(
                id = "user_004",
                name = "刘小姐",
                age = 27,
                location = "北京",
                avatarUrl = "https://example.com/avatar/user004.jpg",
                bio = "文艺青年，喜欢音乐和电影",
                tags = listOf("文艺", "音乐", "电影"),
                photos = emptyList(),
                occupation = "编辑",
                education = "本科",
                height = 160,
                weight = 45,
                isOnline = false,
                distance = "10km",
                lastActiveTime = System.currentTimeMillis() - 7200000
            )
        )
    }
}
