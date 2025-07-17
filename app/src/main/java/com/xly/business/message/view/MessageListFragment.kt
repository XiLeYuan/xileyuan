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
        // mock 数据
        val mockList = listOf(
            Message("1", "小明", "你好，这是一条消息", "12:00"),
            Message("2", "小红", "明天有空吗？", "12:05"),
            Message("3", "系统通知", "欢迎加入新群聊", "13:00"),
            Message("4", "小刚", "图片[1]", "14:20"),
            Message("5", "小美", "哈哈哈", "15:10")
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