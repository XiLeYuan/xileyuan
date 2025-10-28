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

class FindFragment : LYBaseFragment<FragmentFindBinding,RecommendViewModel>() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = "https://image.baidu.com/search/detail?ct=503316480&z=0&tn=baiduimagedetail&ipn=d&cl=2&cm=1&sc=0&sa=vs_ala_img_datu&lm=-1&ie=utf8&pn=0&rn=1&di=7518804108115968001&ln=0&word=%E5%9B%BE%E7%89%87&os=4074351579%2C2126130336&cs=2047808885%2C2267858098&objurl=http%3A%2F%2Fww2.sinaimg.cn%2Fmw690%2F007ut4Uhly1hx4v37mpxcj30u017cgrv.jpg&bdtype=0&simid=3335039407%2C285183104&pi=0&adpicid=0&timingneed=&spn=0&is=0%2C0&lid=cbbfc45100547e69"
        val mockList = listOf(
            Moment(
                "1", R.mipmap.head_img, "结婚吧", "我们结婚啦！",
                listOf(R.mipmap.find_img_2,R.mipmap.find_img_3,R.mipmap.find_img_4,R.mipmap.find_img_3),
                "1分钟前"
            ),
            Moment(
                "2", R.mipmap.head_img, "结婚吧", "我们结婚啦",
                listOf(R.mipmap.find_img_3,R.mipmap.find_img_2,R.mipmap.find_img_4,R.mipmap.stylemax_8,R.mipmap.find_img_1,R.mipmap.stylemax_11),
                "5分钟前"
            ),
            Moment(
                "1", R.mipmap.head_img, "结婚吧", "我们结婚啦！",
                listOf(R.mipmap.find_img_2,R.mipmap.find_img_3,R.mipmap.find_img_1,R.mipmap.find_img_3),
                "1分钟前"
            ),
            Moment(
                "1", R.mipmap.head_img, "小明", "今天心情不错！",
                listOf(R.mipmap.find_img_3,R.mipmap.stylemax_6,R.mipmap.find_img_4,R.mipmap.find_img_1),
                "1分钟前"
            ),
            Moment(
                "1", R.mipmap.head_img, "小明", "今天心情不错！",
                listOf(R.mipmap.find_img_4,R.mipmap.find_img_3,R.mipmap.stylemax_7,R.mipmap.stylemax_8),
                "1分钟前"
            ),
            Moment(
                "1", R.mipmap.head_img, "小明", "今天心情不错！",
                listOf(R.mipmap.find_img_4,R.mipmap.stylemax_6,R.mipmap.find_img_3,R.mipmap.stylemax_8),
                "1分钟前"
            ),
            Moment(
                "1", R.mipmap.head_img, "小明", "今天心情不错！",
                listOf(R.mipmap.find_img_4,R.mipmap.find_img_3,R.mipmap.stylemax_7,R.mipmap.stylemax_8),
                "1分钟前"
            ),
            // ...更多mock数据
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


        viewBind.momentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBind.momentRecyclerView.adapter = MomentAdapter(mockList, requireActivity())

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