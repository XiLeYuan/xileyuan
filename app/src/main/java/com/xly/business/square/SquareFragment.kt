package com.xly.business.square

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.xly.base.LYBaseFragment
import com.xly.business.favorite.view.LikeFragment
import com.xly.business.favorite.view.VisitorFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.view.MomentFragment
import com.xly.databinding.FragmentSquareBinding
import com.xly.ui.widget.CustomLineIndicator
import com.xly.ui.widget.CustomTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

class SquareFragment : LYBaseFragment<FragmentSquareBinding, RecommendViewModel>() {


    // 颜色配置
    private val normalColor = Color.parseColor("#333333")
    private val selectedColor = Color.parseColor("#FF6B6B")
    private val indicatorColor = Color.parseColor("#FF6B6B")

    // 文字大小配置
    private val normalTextSize = 14f
    private val selectedTextSize = 18f

    // 指示器配置
    private val indicatorRadius = 4f

    private val fragments = listOf(
        LikeFragment(),
        VisitorFragment(),
        VisitorFragment(),
        MomentFragment()
    )

    private val tabTitles = listOf("今日精选", "红娘","婚庆","动态")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        initTabLayout()
    }

    private fun initTabLayout() {
        val commonNavigator = CommonNavigator(requireContext())
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val titleView = CustomTitleView(context)
                titleView.text = tabTitles[index]
                titleView.setNormalTextSize(normalTextSize)
                titleView.setSelectedTextSize(selectedTextSize)
                titleView.setNormalColor(normalColor)
                titleView.setSelectedColor(selectedColor)
                titleView.setOnClickListener {
                    viewBind.viewPager.currentItem = index
                }
                return titleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = CustomLineIndicator(context)
                indicator.setLineColor(indicatorColor)
                indicator.roundRadius = indicatorRadius
                return indicator
            }
        }
        viewBind.magicIndicator.navigator = commonNavigator


        viewBind.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewBind.magicIndicator.onPageSelected(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                viewBind.magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                viewBind.magicIndicator.onPageScrollStateChanged(state)
            }
        })
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