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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Mock banner 数据
        val bannerList = listOf(
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
        
        // 获取图片资源ID的辅助函数
        fun getImageResId(name: String): Int {
            return resources.getIdentifier(name, "mipmap", requireContext().packageName)
        }
        
        // 8张头像图片资源
        val imageResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )
        
        // 生成图片资源ID列表
        val imageResIds = imageResources.map { getImageResId(it) }
        
        // 创建不同数量的图片列表，展示不同的布局样式
        val mockList = listOf(
            // 1张图片 - 展示单张大图布局（横图）
            Moment(
                "1", 
                getImageResId("head_one"), 
                "小美", 
                "今天天气真好，出去走走～",
                listOf(imageResIds[0]),
                "刚刚",
                isVertical = false
            ),
            // 1张图片 - 展示单张大图布局（竖图，高大于宽）
            Moment(
                "12", 
                getImageResId("head_three"), 
                "小琳", 
                "今天拍了一张竖图，感觉很不错",
                listOf(imageResIds[0]),
                "刚刚",
                isVertical = true
            ),
            // 2张图片 - 展示并排布局
            Moment(
                "2", 
                getImageResId("head_two"), 
                "小红", 
                "周末和朋友一起聚餐，很开心！",
                listOf(imageResIds[0], imageResIds[1]),
                "2分钟前"
            ),
            // 3张图片 - 展示2+1布局
            Moment(
                "3", 
                getImageResId("head_three"), 
                "小丽", 
                "今天去公园拍照，风景很美",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2]),
                "5分钟前"
            ),
            // 3张图片 - 再次展示特殊布局
            Moment(
                "4", 
                getImageResId("head_four"), 
                "小芳", 
                "和闺蜜一起逛街，买了好多东西",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2]),
                "10分钟前"
            ),
            // 4张图片 - 展示超过3张的"更多"标识（在第三张右下角显示+1）
            Moment(
                "5", 
                getImageResId("head_five"), 
                "小雅", 
                "今天做了好多好吃的，分享给大家",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2], imageResIds[3]),
                "15分钟前"
            ),
            // 3张图片 - 展示特殊布局（第一张最大，第二三张在右侧上下排列）
            Moment(
                "6", 
                getImageResId("head_six"), 
                "小静", 
                "周末旅行，拍了很多美照",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2]),
                "20分钟前"
            ),
            // 5张图片 - 展示超过3张的"更多"标识（在第三张右下角显示+2）
            Moment(
                "7", 
                getImageResId("head_seven"), 
                "小雯", 
                "今天拍了好多照片，每一张都很喜欢",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2], imageResIds[3], imageResIds[4]),
                "30分钟前"
            ),
            // 8张图片 - 展示超过3张的"更多"标识（在第三张右下角显示+5）
            Moment(
                "11", 
                getImageResId("head_eight"), 
                "小云", 
                "周末旅行，拍了很多美照，分享给大家",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2], imageResIds[3], imageResIds[4], imageResIds[5], imageResIds[6], imageResIds[7]),
                "1小时前"
            ),
            // 再次展示不同布局样式
            Moment(
                "8", 
                getImageResId("head_eight"), 
                "小月", 
                "今天心情不错，分享一些日常",
                listOf(imageResIds[0], imageResIds[1]),
                "1小时前"
            ),
            Moment(
                "9", 
                getImageResId("head_one"), 
                "小星", 
                "和朋友们一起度过愉快的周末",
                listOf(imageResIds[0], imageResIds[1], imageResIds[2], imageResIds[3], imageResIds[4]),
                "2小时前"
            ),
            Moment(
                "10", 
                getImageResId("head_two"), 
                "小晴", 
                "今天去看了电影，非常精彩",
                listOf(imageResIds[0]),
                "3小时前",
                isVertical = false
            ),
            // 1张图片 - 再次展示竖图布局
            Moment(
                "13", 
                getImageResId("head_four"), 
                "小欣", 
                "竖图的效果也很不错呢",
                listOf(imageResIds[0]),
                "4小时前",
                isVertical = true
            )
        )

        viewBind.refreshLayout.setRefreshHeader(MaterialHeader(requireActivity()))

        // 设置加载更多底部
        viewBind.refreshLayout.setRefreshFooter(ClassicsFooter(requireActivity()))

        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            // 模拟网络请求
            Handler().postDelayed({
                // 刷新数据
                refreshLayout.finishRefresh()
            }, 2000)
        }

        // 上拉加载更多监听
        viewBind.refreshLayout.setOnLoadMoreListener { refreshLayout ->
            /*loadMoreData { success, hasMore ->
                viewBind.refreshLayout.finishLoadMore(success, hasMore)
            }*/
        }


        val adapter = MomentAdapter(
            mockList, 
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