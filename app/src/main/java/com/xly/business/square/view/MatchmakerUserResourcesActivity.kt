package com.xly.business.square.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jspp.model.UserCard
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.model.Matchmaker
import com.xly.business.square.view.adapter.MatchmakerUserAdapter
import com.xly.business.square.view.adapter.MatchmakerListItem
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.ActivityMatchmakerUserResourcesBinding
import com.xly.middlelibrary.utils.MatchmakerMockData

class MatchmakerUserResourcesActivity : LYBaseActivity<ActivityMatchmakerUserResourcesBinding, RecommendViewModel>() {

    private lateinit var matchmaker: Matchmaker
    private lateinit var userAdapter: MatchmakerUserAdapter
    
    private var currentPage = 1
    private val pageSize = 20
    private var isLoadingMore = false
    private var hasMoreData = true

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
        loadUserResources(isRefresh = false)
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
            
            // 设置刷新布局的paddingTop，避免内容被导航栏遮挡
            // 但不要设置padding，让刷新头部可以正常显示
            val toolbarHeight = viewBind.toolbarContainer.height
            // 只给RecyclerView设置paddingTop，而不是整个SmartRefreshLayout
            viewBind.recyclerView.setPadding(
                viewBind.recyclerView.paddingLeft,
                toolbarHeight + 8.dpToPx(), // 导航栏高度 + 原有padding
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
        userAdapter = MatchmakerUserAdapter { userCard ->
            // 点击用户卡片，跳转到用户详情页
            val intent = Intent(this, LYUserDetailInfoActivity::class.java).apply {
                putExtra("user_id", userCard.id)
            }
            startActivity(intent)
        }

        // 使用线性布局，单列列表
        viewBind.recyclerView.layoutManager = LinearLayoutManager(this)
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
                    outRect.top = spacing
                    outRect.bottom = spacing
                }
            }
        )
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
        viewBind.refreshLayout.setEnableLoadMore(true)
        
        // 下拉刷新
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            currentPage = 1
            hasMoreData = true
            loadUserResources(isRefresh = true) {
                refreshLayout.finishRefresh()
            }
        }
        
        // 加载更多
        viewBind.refreshLayout.setOnLoadMoreListener { refreshLayout ->
            if (!isLoadingMore && hasMoreData) {
                currentPage++
                loadUserResources(isRefresh = false) {
                    refreshLayout.finishLoadMore(hasMoreData)
                }
            } else {
                refreshLayout.finishLoadMore(!hasMoreData)
            }
        }
    }
    
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun loadUserResources(isRefresh: Boolean, onComplete: (() -> Unit)? = null) {
        if (isLoadingMore) {
            onComplete?.invoke()
            return
        }
        
        isLoadingMore = true
        
        // TODO: 从ViewModel或API加载该红娘的用户资源
        // 这里先用Mock数据模拟分页
        val allMockUsers = generateMockUserResources(matchmaker.id)
        val totalCount = allMockUsers.size
        
        // 模拟分页数据
        val startIndex = (currentPage - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, totalCount)
        val pageUsers = if (startIndex < totalCount) {
            allMockUsers.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        // 模拟网络延迟
        viewBind.recyclerView.postDelayed({
            if (isRefresh) {
                // 刷新：构建新列表，红娘信息 + 用户列表
                val listItems = mutableListOf<MatchmakerListItem>()
                listItems.add(MatchmakerListItem.MatchmakerInfo(matchmaker))
                listItems.addAll(pageUsers.map { MatchmakerListItem.UserInfo(it) })
                userAdapter.submitList(listItems)
            } else {
                // 加载更多：追加数据
                val currentList = userAdapter.currentList.toMutableList()
                // 如果当前列表为空或第一个不是红娘信息，先添加红娘信息
                if (currentList.isEmpty() || currentList[0] !is MatchmakerListItem.MatchmakerInfo) {
                    currentList.add(0, MatchmakerListItem.MatchmakerInfo(matchmaker))
                }
                // 追加用户数据
                currentList.addAll(pageUsers.map { MatchmakerListItem.UserInfo(it) })
                userAdapter.submitList(currentList)
            }
            
            // 判断是否还有更多数据
            hasMoreData = endIndex < totalCount
            
            isLoadingMore = false
            onComplete?.invoke()
        }, 500) // 模拟500ms延迟
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

