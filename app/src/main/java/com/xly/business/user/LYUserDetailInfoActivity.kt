package com.xly.business.user

import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.xly.R
import com.xly.databinding.ActivityUserDetailInfoBinding

import com.gyf.immersionbar.ImmersionBar
import com.xly.business.user.adapter.UserImageAdapter


/**
 * 用户详情页 - 微信朋友圈风格
 * 实现效果：
 * 1. 顶部生活照支持左右滑动（ViewPager2）
 * 2. 用户信息卡片覆盖在图片上，白色圆角背景
 * 3. 向上滑动时图片跟随折叠
 * 4. 向下滑动时，图片完全显示后继续下拉有阻尼放大效果（类似朋友圈）
 */

class LYUserDetailInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailInfoBinding
    private lateinit var imageAdapter: UserImageAdapter
    private var imageUrls = mutableListOf<String>()




    // 状态栏相关
    private var isStatusBarDark = false
    private var isAppBarExpanded = true

    // 阻尼效果相关
    private var scale = 1f
    private val maxScale = 1.2f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersionBar()
        setupViewPager()
        setupToolbar()
        setupScrollListeners()
        loadUserData()
    }


    /**
     * 设置沉浸式状态栏 - 优化版
     */
    private fun setupImmersionBar() {
        ImmersionBar.with(this)
            .transparentBar() // 透明状态栏和导航栏
            .statusBarDarkFont(false) // 初始状态栏文字浅色（图片背景时）
            .fitsSystemWindows(false)
            .keyboardEnable(true)
            .init()

        /*ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(false)
            .navigationBarDarkIcon(true) // 导航栏图标深色
            .navigationBarColor(android.R.color.white) // 导航栏背景白色
            .fitsSystemWindows(false)
            .keyboardEnable(true)
            .init()*/
    }

    /**
     * 设置滚动监听
     */
    private fun setupScrollListeners() {
        // AppBarLayout 折叠状态监听
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / totalScrollRange

            // 更新状态栏颜色
//            updateStatusBar(percentage)

            // 更新工具栏背景
//            updateToolbarBackground(percentage)

            setupUnifiedSystemBars()

            // 检查是否完全展开
            if (verticalOffset == 0) {
                setupOverScrollListener()
                isAppBarExpanded = true
            } else if (Math.abs(verticalOffset) >= totalScrollRange) {
                isAppBarExpanded = false
            }
        }

        // NestedScrollView 滚动监听
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            // 处理下拉阻尼效果
            handleOverScroll(scrollY)
        }
    }




    /**
     * 更新工具栏标题显示
     */
    private fun updateToolbarTitle(percentage: Float) {
        val toolbarTitle = binding.toolbar.findViewById<TextView>(R.id.toolbar_title)

        if (percentage > 0.5f) {
            // 显示标题
            if (toolbarTitle.visibility != View.VISIBLE) {
                toolbarTitle.visibility = View.VISIBLE
                toolbarTitle.alpha = 0f
                toolbarTitle.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()

                // 更新标题颜色
                toolbarTitle.setTextColor(if (isStatusBarDark) Color.BLACK else Color.WHITE)
            }
        } else {
            // 隐藏标题
            if (toolbarTitle.visibility == View.VISIBLE) {
                toolbarTitle.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction { toolbarTitle.visibility = View.INVISIBLE }
                    .start()
            }
        }
    }




    private fun setupUnifiedSystemBars() {
        var lastUpdateTime = 0L
        val updateInterval = 16L // 约60fps

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime < updateInterval) {
                return@addOnOffsetChangedListener
            }
            lastUpdateTime = currentTime

            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / totalScrollRange

            // 使用相同的透明度计算确保完全同步
            val alpha = calculateUnifiedAlpha(percentage)

            // 同步更新所有组件
            updateStatusBar(alpha)
            updateNavigationBar(alpha)
            updateToolbar(alpha)
            updateToolbarIcons(alpha)
        }
    }

    private fun calculateUnifiedAlpha(percentage: Float): Float {
        return (percentage - 0.1f).coerceIn(0f, 0.2f) / 0.2f
    }

    private fun updateStatusBar(alpha: Float) {

        val color = calculateColor(alpha)
        val isDark = alpha > 0.5f

        ImmersionBar.with(this)
            .statusBarDarkFont(isDark)
            .statusBarColorInt(color) // 使用 statusBarColorInt 而不是 statusBarColor
            .init()
    }

    private fun updateNavigationBar(alpha: Float) {
        val color = calculateColor(alpha)
        val isDark = alpha > 0.5f

        ImmersionBar.with(this)
            .navigationBarDarkIcon(isDark)
            .navigationBarColorInt(color) // 使用 navigationBarColorInt
            .init()
    }

    private fun calculateColor(alpha: Float): Int {
        return Color.argb(
            (alpha * 255).toInt(),
            255, 255, 255  // 白色
        )
    }

    private fun updateToolbar(alpha: Float) {
        val color = calculateColor(alpha)
        binding.toolbar.setBackgroundColor(color)
    }

    private fun updateToolbarIcons(alpha: Float) {

        val isDark = alpha > 0.5f
        val iconRes = if (isDark) {
            R.mipmap.main_back_icon_press
        } else {
            R.mipmap.main_back_icon
        }

        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, iconRes)

    }


    private fun updateToolbarBackground(percentage: Float) {

    }












    /**
     * 处理下拉阻尼效果
     */
    private fun handleOverScroll(scrollY: Int) {
        if (isAppBarExpanded && scrollY < 0) {
            // 下拉状态，计算阻尼放大效果
            val overscroll = Math.abs(scrollY)
            scale = 1 + (overscroll * 0.002f).coerceAtMost(maxScale - 1)

            // 应用缩放动画
            binding.viewPager.scaleX = scale
            binding.viewPager.scaleY = scale
            binding.viewPager.pivotX = binding.viewPager.width / 2f
            binding.viewPager.pivotY = 0f
        } else if (scale > 1f) {
            // 恢复原大小
            scale = 1f
            binding.viewPager.scaleX = scale
            binding.viewPager.scaleY = scale
        }
    }


    /**
     * 设置图片轮播
     */
    private fun setupViewPager() {
        imageAdapter = UserImageAdapter()
        binding.viewPager.adapter = imageAdapter

        // 设置指示器
        setupIndicators()

        // 页面变化监听
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
    }

    /**
     * 设置指示器
     */
    private fun setupIndicators() {
        binding.indicatorLayout.removeAllViews()

        for (i in imageUrls.indices) {
            val imageView = ImageView(this).apply {
                val params = LinearLayout.LayoutParams(
                   15,
                    15
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                layoutParams = params
                setImageResource(if (i == 0) R.drawable.indicator_dot else R.drawable.indicator_dot_inactive)
            }
            binding.indicatorLayout.addView(imageView)
        }
    }

    /**
     * 更新指示器
     */
    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorLayout.childCount) {
            val imageView = binding.indicatorLayout.getChildAt(i) as ImageView
            imageView.setImageResource(if (i == position) R.drawable.indicator_dot else R.drawable.indicator_dot_inactive)
        }
    }

    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 设置返回按钮图标
        binding.toolbar.setNavigationIcon(R.mipmap.main_back_icon)
    }

    /**
     * 设置下拉阻尼放大效果
     */
    private fun setupDampingEffect() {
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / totalScrollRange

            // 更新工具栏透明度
            updateToolbarAlpha(percentage)

            // 检查是否完全展开
            if (verticalOffset == 0) {
                setupOverScrollListener()
            }
        }
    }

    /**
     * 设置过度滚动监听
     */
    private fun setupOverScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY < 0) {
                // 下拉状态，计算阻尼放大效果
                val overscroll = Math.abs(scrollY)
                scale = 1 + (overscroll * 0.002f).coerceAtMost(maxScale - 1)

                // 应用缩放动画
                binding.viewPager.scaleX = scale
                binding.viewPager.scaleY = scale

                // 保持图片居中
                binding.viewPager.pivotX = binding.viewPager.width / 2f
                binding.viewPager.pivotY = 0f
            } else if (scale > 1f) {
                // 恢复原大小
                scale = 1f
                binding.viewPager.scaleX = scale
                binding.viewPager.scaleY = scale
            }
        }
    }

    /**
     * 更新工具栏透明度
     */
    private fun updateToolbarAlpha(percentage: Float) {
        val alpha = (percentage * 255).toInt()
        binding.toolbar.alpha = percentage
    }

    /**
     * 加载用户数据
     */
    private fun loadUserData() {
        // 模拟数据
        imageUrls = mutableListOf(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
        )

        imageAdapter.submitList(imageUrls)
        setupIndicators()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
