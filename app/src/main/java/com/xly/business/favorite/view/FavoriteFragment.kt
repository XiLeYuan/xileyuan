package com.xly.business.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentFavoriteBinding
import kotlin.jvm.java

class FavoriteFragment : LYBaseFragment<FragmentFavoriteBinding,RecommendViewModel>() {

    private val fragments = listOf(
        LikeFragment(),
        VisitorFragment()
    )

    private val tabTitles = listOf("喜欢我", "访客")

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
    ): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}