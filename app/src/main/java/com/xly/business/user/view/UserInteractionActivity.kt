package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.ActivityUserInteractionBinding
import com.xly.middlelibrary.utils.LYFontUtil
import com.xly.ui.widget.CustomLineIndicator
import com.xly.ui.widget.CustomTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

class UserInteractionActivity : LYBaseActivity<ActivityUserInteractionBinding, RecommendViewModel>() {

    companion object {
        private const val EXTRA_INITIAL_TAB = "initial_tab"
        
        fun start(context: Context, initialTab: Int = 0) {
            val intent = Intent(context, UserInteractionActivity::class.java).apply {
                putExtra(EXTRA_INITIAL_TAB, initialTab)
            }
            context.startActivity(intent)
        }
    }

    // 颜色配置
    private val normalColor = Color.parseColor("#000000")
    private val selectedColor = Color.parseColor("#000000")
    private val indicatorColor = Color.parseColor("#FF6B6B")

    // 文字大小配置
    private val normalTextSize = 15f
    private val selectedTextSize = 19f

    // 指示器配置
    private val indicatorRadius = 4f

    private val fragments = listOf(
        LikedPeopleFragment(),
        VisitorsFragment(),
        WhoLikedMeFragment()
    )

    private val tabTitles = listOf("喜欢的人", "访客", "谁喜欢我")

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityUserInteractionBinding {
        return ActivityUserInteractionBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(this)[RecommendViewModel::class.java]
    }

    override fun initView() {
        setupViewPager()
        setupTabLayout()
        
        // 设置初始选中的tab
        val initialTab = intent.getIntExtra(EXTRA_INITIAL_TAB, 0)
        if (initialTab in 0 until fragments.size) {
            viewBind.viewPager.setCurrentItem(initialTab, false)
        }
    }

    override fun initOnClick() {
        // 返回按钮
        viewBind.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setupViewPager() {
        viewBind.viewPager.adapter = object : FragmentStateAdapter(this as FragmentActivity) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }
    }

    private fun setupTabLayout() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val titleView = CustomTitleView(context)
                titleView.typeface = LYFontUtil.getMediumFont(context)
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

            override fun getIndicator(context: Context): IPagerIndicator? {
                val indicator = CustomLineIndicator(context)
                indicator.roundRadius = indicatorRadius
                indicator.setBottomOffset(1f)  // 1dp，让指示器更靠近文本
                return indicator
            }
        }
        viewBind.magicIndicator.navigator = commonNavigator

        viewBind.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewBind.magicIndicator.onPageSelected(position)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                viewBind.magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                viewBind.magicIndicator.onPageScrollStateChanged(state)
            }
        })
    }
}

