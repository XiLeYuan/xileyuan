package com.xly.business.vip.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import com.xly.base.LYBaseActivity
import com.xly.databinding.ActivityStarMeBinding
import com.xly.index.viewmodel.MainViewModel
import kotlin.math.abs

class LookStarMeActivity : LYBaseActivity<ActivityStarMeBinding, MainViewModel>() {

    companion object {
        fun start(c: Context) {
            val intent = Intent(c, LookStarMeActivity::class.java)
            (c as? Activity)?.startActivity(intent)
        }
    }

    private var isLightStatusBar = false

    override fun initView() {
        // 顶部透明状态栏，文字使用浅色
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .transparentStatusBar()
            .init()

        Glide.with(this).load(com.xly.R.mipmap.banner_who_likes_me)
            .into(viewBind.ivBanner)
        setupViewPagerAndTabs()
        setupAppBarOffset()

        viewBind.ivBack.setOnClickListener { finish() }
    }

    private fun setupViewPagerAndTabs() {
        val fragments: List<Fragment> = listOf(
            WhoLikesMeFragment(),
            VipTabFragment(),
            SvipTabFragment()
        )
        val titles = listOf("谁喜欢我", "VIP", "SVIP")

        viewBind.viewPager.offscreenPageLimit = fragments.size
        viewBind.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }
        viewBind.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        
        viewBind.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val bannerResId = when (position) {
                    0 -> com.xly.R.mipmap.banner_who_likes_me
                    1 -> com.xly.R.mipmap.banner_vip
                    2 -> com.xly.R.mipmap.banner_svip
                    else -> com.xly.R.mipmap.banner_who_likes_me
                }
                Glide.with(this@LookStarMeActivity).load(bannerResId).into(viewBind.ivBanner)
            }
        })

        TabLayoutMediator(viewBind.tabLayout, viewBind.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    private fun setupAppBarOffset() {
        viewBind.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                if (totalScrollRange == 0) return@OnOffsetChangedListener

                val fraction = abs(verticalOffset) / totalScrollRange.toFloat()
                val isCollapsed = fraction > 0.6f

                if (isCollapsed) {
                    viewBind.tabLayout.setTabTextColors(
                        resources.getColor(android.R.color.darker_gray),
                        resources.getColor(android.R.color.black)
                    )
                    if (!isLightStatusBar) {
                        ImmersionBar.with(this)
                            .statusBarColor(android.R.color.white)
                            .statusBarDarkFont(true)
                            .init()
                        viewBind.ivBack.setImageResource(com.xly.R.mipmap.ic_arrow_left_black)
                        isLightStatusBar = true
                    }
                } else {
                    viewBind.tabLayout.setTabTextColors(
                        resources.getColor(android.R.color.system_surface_container_low_light),
                        resources.getColor(android.R.color.white)
                    )
                    if (isLightStatusBar) {
                        ImmersionBar.with(this)
                            .transparentStatusBar()
                            .statusBarDarkFont(false)
                            .init()
                        viewBind.ivBack.setImageResource(com.xly.R.mipmap.ic_arrow_left_white)
                        isLightStatusBar = false
                    }
                }
            }
        )
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityStarMeBinding {
        return ActivityStarMeBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }
}