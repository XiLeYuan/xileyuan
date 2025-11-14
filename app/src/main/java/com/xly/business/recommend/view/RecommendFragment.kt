package com.xly.business.recommend.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding
import com.xly.middlelibrary.utils.LYFontUtil
import com.xly.middlelibrary.utils.click

class RecommendFragment : LYBaseFragment<FragmentRecommendBinding, RecommendViewModel>() {

    private val fragments = listOf(
        RecommendContentFragment(),
        HometownFragment()
    )

    private val tabTitles = listOf("推荐", "同乡")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
    }

    private var currentSelectedTab = 0

    private fun setupViewPager() {
        viewBind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }
        // 禁用ViewPager2的横向滑动，只允许通过Tab点击切换
        viewBind.viewPager.isUserInputEnabled = false
        
        // 监听ViewPager2的页面变化
        viewBind.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabSelection(position)
            }
        })
    }

    private fun setupTabLayout() {
        // 设置Tab点击事件
        viewBind.tabRecommend.setOnClickListener {
            if (currentSelectedTab != 0) {
                viewBind.viewPager.currentItem = 0
            }
        }
        
        viewBind.tabHometown.setOnClickListener {
            if (currentSelectedTab != 1) {
                viewBind.viewPager.currentItem = 1
            }
        }
        
        // 初始化第一个tab为选中状态
        updateTabSelection(0)
    }
    
    private fun updateTabSelection(position: Int) {
        val previousPosition = currentSelectedTab
        currentSelectedTab = position
        
        // 获取目标Tab View
        val targetTab = if (position == 0) viewBind.tabRecommend else viewBind.tabHometown
        val previousTab = if (previousPosition == 0) viewBind.tabRecommend else viewBind.tabHometown
        
        // 更新文字颜色
        viewBind.tabRecommend.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (position == 0) android.R.color.white else R.color.text_secondary
            )
        )
        viewBind.tabHometown.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (position == 1) android.R.color.white else R.color.text_secondary
            )
        )
        
        // 动画移动选中背景
        animateSelectedBackground(targetTab)
    }
    
    private fun animateSelectedBackground(targetTab: TextView) {
        val backgroundView = viewBind.tabSelectedBackground
        val container = viewBind.tabContainer
        
        // 获取目标Tab的位置和尺寸
        targetTab.post {
            val targetLeft = targetTab.left.toFloat()
            val targetWidth = targetTab.width.toFloat()
            
            // 计算容器的高度和选中背景应该的高度（容器高度减去padding）
            val containerHeight = container.height.toFloat()
            val containerPadding = container.paddingTop + container.paddingBottom
            val backgroundHeight = containerHeight - containerPadding
            
            // 计算竖直居中的位置（容器的paddingTop + 背景应该的y位置）
            val containerPaddingTop = container.paddingTop.toFloat()
            val targetTop = containerPaddingTop
            
            // 设置背景View的初始位置和尺寸
            if (backgroundView.visibility == View.INVISIBLE) {
                // 首次显示，直接设置位置
                backgroundView.x = targetLeft
                backgroundView.y = targetTop
                val params = backgroundView.layoutParams
                params.width = targetWidth.toInt()
                params.height = backgroundHeight.toInt()
                backgroundView.layoutParams = params
                backgroundView.visibility = View.VISIBLE
            } else {
                // 已有位置，使用动画移动
                val startX = backgroundView.x
                val startY = backgroundView.y
                val startWidth = backgroundView.width.toFloat()
                val startHeight = backgroundView.height.toFloat()
                
                val animatorX = ObjectAnimator.ofFloat(backgroundView, "x", startX, targetLeft)
                val animatorY = ObjectAnimator.ofFloat(backgroundView, "y", startY, targetTop)
                
                // 使用ValueAnimator来动画化宽度和高度
                val widthAnimator = android.animation.ValueAnimator.ofFloat(startWidth, targetWidth)
                widthAnimator.addUpdateListener { animator ->
                    val params = backgroundView.layoutParams
                    params.width = (animator.animatedValue as Float).toInt()
                    backgroundView.layoutParams = params
                }
                
                val heightAnimator = android.animation.ValueAnimator.ofFloat(startHeight, backgroundHeight)
                heightAnimator.addUpdateListener { animator ->
                    val params = backgroundView.layoutParams
                    params.height = (animator.animatedValue as Float).toInt()
                    backgroundView.layoutParams = params
                }
                
                android.animation.AnimatorSet().apply {
                    playTogether(animatorX, animatorY, widthAnimator, heightAnimator)
                    duration = 250
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
        }
    }

    override fun initView() {
        // titleName已移除，不再需要设置
    }

    override fun initOnClick() {
        // 筛选入口点击事件
        viewBind.ivFilter.click {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val filterDialog = FilterBottomSheetDialogFragment.newInstance()
        filterDialog.onConfirmClick = { options ->
            // TODO: 根据筛选条件过滤数据
            // 这里可以根据筛选条件重新加载数据
        }
        filterDialog.show(parentFragmentManager, "FilterBottomSheetDialogFragment")
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBinding {
        return FragmentRecommendBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return androidx.lifecycle.ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }

    override fun initObservers() {
        // 可以在这里添加ViewModel的观察者
    }
}
