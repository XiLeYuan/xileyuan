package com.xly.business.square

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.xly.base.LYBaseFragment
import com.xly.business.favorite.view.LikeFragment
import com.xly.business.favorite.view.VisitorFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.view.FindFragment
import com.xly.databinding.FragmentSquareBinding

class SquareFragment : LYBaseFragment<FragmentSquareBinding, RecommendViewModel>() {

    private val fragments = listOf(
        LikeFragment(),
        VisitorFragment(),
        VisitorFragment(),
        FindFragment()
    )

    private val tabTitles = listOf("今日精选", "人工红娘","婚庆服务","动态")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }
        TabLayoutMediator(viewBind.tabLayout, viewBind.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSquareBinding {
        return FragmentSquareBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}