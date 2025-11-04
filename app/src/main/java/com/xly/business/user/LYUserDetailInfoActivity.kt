package com.xly.business.user

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.gyf.immersionbar.ImmersionBar
import com.xly.R
import com.xly.business.user.adapter.UserImageAdapter
import com.xly.databinding.ActivityUserDetailInfoBinding

/**
 * 用户详情页 - 微信朋友圈风格（性能优化版）
 * 优化点：
 * 1. 避免频繁调用 ImmersionBar.init()，使用系统 API 直接更新
 * 2. 添加节流机制，限制更新频率
 * 3. 缓存颜色值，避免重复设置
 * 4. 确保导航栏与状态栏渐变同步
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

    // 性能优化：缓存上一次的颜色值，避免重复设置
    private var lastStatusBarColor = Color.TRANSPARENT
    private var lastNavigationBarColor = Color.TRANSPARENT
    private var lastStatusBarDarkFont = false
    private var lastNavigationBarDarkIcon = false
    private var lastToolbarAlpha = 0f

    // 性能优化：节流机制
    private var lastUpdateTime = 0L
    private val updateInterval = 16L // 约60fps，16ms更新一次

    // 性能优化：WindowInsetsController 缓存
    private val windowInsetsController by lazy {
        ViewCompat.getWindowInsetsController(window.decorView)
    }

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
     * 设置沉浸式状态栏 - 只初始化一次
     */
    private fun setupImmersionBar() {
        ImmersionBar.with(this)
            .transparentBar() // 透明状态栏和导航栏
            .statusBarDarkFont(false) // 初始状态栏文字浅色（图片背景时）
            .fitsSystemWindows(false)
            .keyboardEnable(true)
            .init()
    }

    /**
     * 设置滚动监听 - 优化版
     */
    private fun setupScrollListeners() {
        // AppBarLayout 折叠状态监听
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = Math.abs(verticalOffset).toFloat() / totalScrollRange

            // 使用节流机制更新系统栏
            updateSystemBarsWithThrottle(percentage)

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
     * 使用节流机制更新系统栏 - 性能优化
     */
    private fun updateSystemBarsWithThrottle(percentage: Float) {
        val currentTime = System.currentTimeMillis()

        // 节流：限制更新频率
        if (currentTime - lastUpdateTime < updateInterval) {
            return
        }
        lastUpdateTime = currentTime

        // 计算统一的透明度
        val alpha = calculateUnifiedAlpha(percentage)

        // 同步更新所有组件
        updateSystemBars(alpha)
    }

    /**
     * 计算统一的透明度值
     */
    private fun calculateUnifiedAlpha(percentage: Float): Float {
        // 从 10% 开始渐变，到 30% 完成
        return ((percentage - 0.1f).coerceIn(0f, 0.2f) / 0.2f).coerceIn(0f, 1f)
    }

    /**
     * 更新系统栏（状态栏和导航栏）- 使用系统 API，避免 ImmersionBar.init()
     */
    private fun updateSystemBars(alpha: Float) {
        val color = calculateColor(alpha)
        val isDark = alpha > 0.5f

        // 性能优化：只在颜色或状态变化时更新
        if (lastStatusBarColor != color) {
            window.statusBarColor = color
            lastStatusBarColor = color
        }

        if (lastNavigationBarColor != color) {
            window.navigationBarColor = color
            lastNavigationBarColor = color
        }

        // 性能优化：只在状态变化时更新文字颜色
        if (lastStatusBarDarkFont != isDark) {
            windowInsetsController?.isAppearanceLightStatusBars = isDark
            lastStatusBarDarkFont = isDark
        }

        if (lastNavigationBarDarkIcon != isDark) {
            windowInsetsController?.isAppearanceLightNavigationBars = isDark
            lastNavigationBarDarkIcon = isDark
        }

        // 更新 Toolbar 背景和图标
        updateToolbar(alpha, isDark)
    }

    /**
     * 计算颜色值
     */
    private fun calculateColor(alpha: Float): Int {
        return Color.argb(
            (alpha * 255).toInt(),
            255, 255, 255  // 白色
        )
    }

    /**
     * 更新 Toolbar - 优化版
     */
    private fun updateToolbar(alpha: Float, isDark: Boolean) {
        val color = calculateColor(alpha)

        // 性能优化：只在颜色变化时更新背景
        if (binding.toolbar.background == null ||
            (binding.toolbar.background as? android.graphics.drawable.ColorDrawable)?.color != color) {
            binding.toolbar.setBackgroundColor(color)
        }

        // 性能优化：只在透明度变化时更新
        if (Math.abs(binding.toolbar.alpha - alpha) > 0.01f) {
            binding.toolbar.alpha = alpha
            lastToolbarAlpha = alpha
        }

        // 更新返回按钮图标
        val iconRes = if (isDark) {
            R.mipmap.main_back_icon_press
        } else {
            R.mipmap.main_back_icon
        }

        // 性能优化：只在图标需要变化时更新
        val currentIcon = binding.toolbar.navigationIcon
        val expectedIconResId = if (isDark) R.mipmap.main_back_icon_press else R.mipmap.main_back_icon

        // 检查是否需要更新图标（简化检查，避免频繁创建 Drawable）
        if (currentIcon == null ||
            (currentIcon as? androidx.appcompat.graphics.drawable.DrawableWrapper)?.wrappedDrawable == null) {
            binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, iconRes)
        }

        // 更新工具栏标题显示
        updateToolbarTitle(alpha)
    }

    /**
     * 更新工具栏标题显示
     */
    private fun updateToolbarTitle(alpha: Float) {
        val toolbarTitle = binding.toolbar.findViewById<TextView>(R.id.toolbar_title) ?: return

        if (alpha > 0.5f) {
            // 显示标题
            if (toolbarTitle.visibility != View.VISIBLE) {
                toolbarTitle.visibility = View.VISIBLE
                toolbarTitle.alpha = 0f
                toolbarTitle.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
            toolbarTitle.setTextColor(if (alpha > 0.5f) Color.BLACK else Color.WHITE)
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

    /**
     * 处理下拉阻尼效果
     */
    private fun handleOverScroll(scrollY: Int) {
        if (isAppBarExpanded && scrollY < 0) {
            // 下拉状态，计算阻尼放大效果
            val overscroll = Math.abs(scrollY)
            val newScale = 1 + (overscroll * 0.002f).coerceAtMost(maxScale - 1)

            // 性能优化：只在缩放值变化时更新
            if (Math.abs(scale - newScale) > 0.01f) {
                scale = newScale
                binding.viewPager.scaleX = scale
                binding.viewPager.scaleY = scale
                binding.viewPager.pivotX = binding.viewPager.width / 2f
                binding.viewPager.pivotY = 0f
            }
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
                val params = LinearLayout.LayoutParams(15, 15).apply {
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
            imageView.setImageResource(
                if (i == position) R.drawable.indicator_dot
                else R.drawable.indicator_dot_inactive
            )
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
     * 设置过度滚动监听
     */
    private fun setupOverScrollListener() {
        // 已经在 setupScrollListeners 中设置，这里不需要重复设置
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
