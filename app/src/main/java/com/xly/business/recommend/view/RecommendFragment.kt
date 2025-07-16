package com.xly.business.recommend.view

import CardAdapter
import Person
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding
import com.xly.R


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {


    private lateinit var adapter: CardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 垂直方向 LinearLayoutManager
        viewBind.recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        // 2. 分页效果
        PagerSnapHelper().attachToRecyclerView(viewBind.recyclerView)

        // 3. 数据
        val data = listOf(
            Person("张三", "热爱编程，Android开发者", R.mipmap.ic_launcher),
            Person("李四", "产品经理，喜欢摄影", R.mipmap.ic_launcher),
            Person("王五", "设计师，极简主义", R.mipmap.ic_launcher)
            // ...更多数据
        )
        adapter = CardAdapter(data)
        viewBind.recyclerView.adapter = adapter
    }


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
                Toast.makeText(requireContext(),"加载失败: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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