package com.xly.business.square.view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}