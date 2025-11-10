package com.xly.business.message.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.base.LYBaseFragment
import com.xly.business.message.model.Message
import com.xly.business.message.view.adapter.MessageAdapter
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentMessageListBinding
import kotlin.jvm.java

class MessageListFragment  : LYBaseFragment<FragmentMessageListBinding,RecommendViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 8张头像图片资源，循环使用
        val avatarResources = listOf(
            "head_one",
            "head_two",
            "head_three",
            "head_four",
            "head_five",
            "head_six",
            "head_seven",
            "head_eight"
        )
        
        // mock 数据
        val mockList = listOf(
            Message("1", "小明", "你好，这是一条消息", "12:00", avatarResources[0]), // head_one
            Message("2", "小红", "明天有空吗？", "12:05", avatarResources[1]), // head_two
            Message("3", "系统通知", "欢迎加入新群聊", "13:00", avatarResources[2]), // head_three
            Message("4", "小刚", "图片[1]", "14:20", avatarResources[3]), // head_four
            Message("5", "小美", "哈哈哈", "15:10", avatarResources[4]) ,// head_five
            Message("6", "小美", "哈哈哈", "15:10", avatarResources[5]) ,// head_five
            Message("7", "小美", "哈哈哈", "15:10", avatarResources[6]) ,// head_five
            Message("8", "小美", "哈哈哈", "15:10", avatarResources[7]), // head_five
            Message("9", "小美", "哈哈哈", "15:10", avatarResources[8]) ,// head_five
            Message("10", "小美", "哈哈哈", "15:10", avatarResources[9]) ,// head_five
        )
        viewBind.messageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBind.messageRecyclerView.adapter = MessageAdapter(mockList)
    }




    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMessageListBinding {
        return FragmentMessageListBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}