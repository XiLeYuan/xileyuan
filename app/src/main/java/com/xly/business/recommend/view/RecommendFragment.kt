package com.xly.business.recommend.view

import CardAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding
import com.xly.R
import com.xly.business.recommend.model.Person
import com.xly.business.user.UserInfo
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.middlelibrary.utils.click
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardStackLayoutManager = CardStackLayoutManager(requireActivity())
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL)
        viewBind.cardStackView.layoutManager = cardStackLayoutManager
        cardStackLayoutManager.setStackFrom(StackFrom.TopAndLeft)

        val data = listOf(
            Person("张三", 25, "热爱运动，喜欢旅行", R.mipmap.head_img),
            Person("李四", 28, "产品经理，喜欢摄影", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("张三", 25, "热爱运动，喜欢旅行", R.mipmap.head_img),
            Person("李四", 28, "产品经理，喜欢摄影", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img),
            Person("王五", 22, "设计师，极简主义", R.mipmap.head_img)
            // ...更多数据
        )
        viewBind.cardStackView.adapter = CardAdapter(data)
    }


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
                Toast.makeText(requireContext(),"加载失败: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun initView() {
        viewBind.topNav.topImgOne.setImageResource(R.mipmap.msf)
        viewBind.topNav.topImgTwo.setImageResource(R.mipmap.msc)
    }

    override fun initOnClick() {
        viewBind.chatImg.click {

//            LookStarMeActivity.start(requireActivity())

            val testUser = UserInfo(
                id = "test_001",
                name = "测试用户",
                age = 25,
                height = 165,
                weight = 50,
                occupation = "软件工程师",
                education = "硕士",
                bio = "这是一个测试用户，用于演示详情页功能。喜欢编程、阅读、旅行，希望找到一个志同道合的伴侣。",
                hobbies = listOf("编程", "阅读", "旅行", "摄影", "健身", "音乐"),
                wechat = "test_user_001",
                qq = "123456789",
                avatarUrl = "https://example.com/test_avatar.jpg"
            )

            // 直接启动详情页（不经过转场动画）

            UserDetailActivity.start(requireActivity())




        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBinding {
        return FragmentRecommendBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}