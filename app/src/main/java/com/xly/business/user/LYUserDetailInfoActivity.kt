package com.xly.business.user

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.xly.R
import com.xly.business.user.adapter.ImagePagerAdapter
import com.xly.business.user.adapter.ThumbnailAdapter

class LYUserDetailInfoActivity : AppCompatActivity() {
    private val primaryColor = Color.parseColor("#FF6B6B") // 主题色珊瑚红
    private var collapsingToolbar: CollapsingToolbarLayout? = null
    private var toolbar: Toolbar? = null
    private var statusToolbarBackground: View? = null
    private var lastAppliedColor: Int = Color.TRANSPARENT // 缓存上次应用的颜色，避免不必要的更新
    private var thumbnailAdapter: ThumbnailAdapter? = null
    private var thumbnailRecycler: RecyclerView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 在 setContentView 之前设置窗口标志，确保状态栏颜色可以绘制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // 设置状态栏为透明，让 statusBarScrim 来控制状态栏颜色
            window.statusBarColor = Color.TRANSPARENT
        }
        
        setContentView(R.layout.activity_detail)

        val intent = intent
        val cheeseName = intent.getStringExtra(EXTRA_NAME)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbar = findViewById(R.id.collapsing_toolbar)
        // 禁用CollapsingToolbarLayout的默认title，使用自定义TextView
        collapsingToolbar?.title = ""
        
        // 设置自定义title的位置和内容

        
        // 获取占位View，作为状态栏和Toolbar的整体背景
        statusToolbarBackground = findViewById(R.id.status_toolbar_background)
        
        // 设置占位View的高度：状态栏高度 + Toolbar高度
        setupStatusToolbarBackground()

        val appBarLayout: AppBarLayout = findViewById(R.id.appbar)
        
        // 监听 AppBarLayout 的滚动偏移，实现状态栏和 Toolbar 红色渐变
        appBarLayout.addOnOffsetChangedListener { appBar, verticalOffset ->
            val totalScrollRange = appBar.totalScrollRange
            val scrollRatio = if (totalScrollRange != 0) {
                (-verticalOffset).toFloat() / totalScrollRange
            } else {
                0f
            }
            // 限制在 0-1 之间
            val clampedRatio = scrollRatio.coerceIn(0f, 1f)
            // 检查是否完全折叠：verticalOffset 的绝对值等于 totalScrollRange
            val isFullyCollapsed = totalScrollRange != 0 && kotlin.math.abs(verticalOffset) >= totalScrollRange
            updateStatusBarColor(clampedRatio, isFullyCollapsed)
            updateThumbnailAlpha(clampedRatio)
        }

        setupImagePager()
    }

    private fun setupStatusToolbarBackground() {
        statusToolbarBackground?.let { background ->
            // 等待布局完成后再设置高度和位置
            background.post {
                val statusBarHeight = getStatusBarHeight()
                
                // 获取Toolbar的实际高度
                // Toolbar使用?attr/actionBarSize，通常是56dp，但为了确保完全覆盖，使用实际测量的高度
                val toolbarHeightPx = toolbar?.height ?: run {
                    // 如果Toolbar还没有测量完成，使用actionBarSize的标准值（56dp）
                    val actionBarSizeAttr = intArrayOf(android.R.attr.actionBarSize)
                    val typedArray = obtainStyledAttributes(actionBarSizeAttr)
                    val actionBarSize = typedArray.getDimensionPixelSize(0, 0)
                    typedArray.recycle()
                    actionBarSize
                }
                
                // 为了确保完全覆盖，稍微增加一点高度（增加2dp作为安全边距）
                val extraHeight = (2 * resources.displayMetrics.density).toInt()
                val totalHeight = statusBarHeight + toolbarHeightPx + extraHeight
                
                val layoutParams = background.layoutParams
                layoutParams.height = totalHeight
                
                // 由于占位View在CollapsingToolbarLayout内部，并且CollapsingToolbarLayout有fitsSystemWindows="true"
                // CollapsingToolbarLayout的内容区域从状态栏下方开始
                // 占位View需要向上偏移状态栏高度，才能覆盖状态栏区域
                // 使用负的marginTop让View向上延伸到状态栏区域
                if (layoutParams is android.view.ViewGroup.MarginLayoutParams) {
                    // 确保负的marginTop能够完全覆盖状态栏
                    layoutParams.topMargin = -statusBarHeight
                }
                background.layoutParams = layoutParams
                
                // 确保占位View在Toolbar下方，作为背景层
                // 由于占位View在布局中位于Toolbar之前，它会在Toolbar下方绘制
                // Toolbar的背景是透明的，所以占位View的颜色会显示出来
            }
        }
    }

    private fun getStatusBarHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowInsets = window.decorView.rootWindowInsets
            windowInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
        } else {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            result
        }
    }

    private fun updateStatusBarColor(scrollRatio: Float, isFullyCollapsed: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        
        val background = this.statusToolbarBackground ?: return
        
        // 调整渐变时机，让状态栏和 Toolbar 的渐变完全同步
        // 当滚动比例达到这个阈值时，开始从透明渐变到红色
        val threshold = 0.7f
        
        // 获取红色的 RGB 分量（不包含 alpha）
        val red = Color.red(primaryColor)
        val green = Color.green(primaryColor)
        val blue = Color.blue(primaryColor)
        
        val finalColor: Int
        val currentAlpha: Int
        
        if (isFullyCollapsed || scrollRatio >= 1.0f) {
            // 完全折叠时，使用完全不透明的红色
            finalColor = primaryColor
            currentAlpha = 255
        } else if (scrollRatio < threshold) {
            // 在阈值之前，保持完全透明
            finalColor = Color.TRANSPARENT
            currentAlpha = 0
        } else {
            // 在阈值之后，计算渐变（在 threshold 到 1.0 之间）
            val gradientRatio = ((scrollRatio - threshold) / (1.0f - threshold)).coerceIn(0f, 1f)
            // 使用平滑插值函数，减少颜色突变，使渐变更平滑
            val smoothRatio = gradientRatio * gradientRatio * (3f - 2f * gradientRatio) // smoothstep
            currentAlpha = (smoothRatio * 255).toInt().coerceIn(0, 255)
            
            // 计算最终颜色（带透明度）
            finalColor = Color.argb(currentAlpha, red, green, blue)
        }
        
        // 防抖机制：只在颜色变化超过阈值时才更新，减少频繁更新导致的闪烁
        // 在关键状态（完全透明或完全不透明）时总是更新
        // 在渐变过程中，只在 alpha 值变化超过 8 时才更新（减少更新频率）
        val lastAlpha = Color.alpha(lastAppliedColor)
        val shouldUpdate = when {
            currentAlpha == 0 || currentAlpha == 255 -> {
                // 关键状态：总是更新
                finalColor != lastAppliedColor
            }
            kotlin.math.abs(currentAlpha - lastAlpha) >= 8 -> {
                // 渐变状态：只在变化超过阈值时更新
                true
            }
            else -> {
                // 变化太小，跳过更新
                false
            }
        }
        
        if (!shouldUpdate) {
            return
        }
        
        // 更新缓存
        lastAppliedColor = finalColor
        
        // 直接设置占位View的背景色，作为状态栏和Toolbar的整体背景
        // 占位View已经覆盖了状态栏和Toolbar区域，所以只需要设置占位View的颜色
        // 状态栏保持透明，让占位View的颜色显示出来，确保状态栏和Toolbar颜色完全一致
        background.setBackgroundColor(finalColor)
        
        // 保持状态栏透明，让占位View的颜色显示出来
        // 如果同时设置window.statusBarColor，会导致颜色叠加，造成状态栏颜色更深
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 根据滚动比例更新缩略图的透明度
     * @param scrollRatio 滚动比例，0表示完全展开，1表示完全折叠
     */
    private fun updateThumbnailAlpha(scrollRatio: Float) {
        thumbnailRecycler?.let { recycler ->
            // 当滚动比例增加时，alpha从1.0逐渐减少到0.0
            // 可以设置一个阈值，在滚动到一定比例时开始渐变
            val fadeStartThreshold = 0.3f // 从30%滚动比例开始渐变
            val fadeEndThreshold = 0.7f // 到70%滚动比例时完全消失
            
            val alpha = when {
                scrollRatio < fadeStartThreshold -> 1.0f // 完全展开时，完全不透明
                scrollRatio > fadeEndThreshold -> 0.0f // 超过阈值时，完全透明
                else -> {
                    // 在阈值之间进行线性插值
                    val fadeProgress = (scrollRatio - fadeStartThreshold) / (fadeEndThreshold - fadeStartThreshold)
                    1.0f - fadeProgress // 从1.0渐变到0.0
                }
            }
            
            recycler.alpha = alpha
            // 当完全透明时，可以设置为不可见以优化性能
            recycler.visibility = if (alpha <= 0f) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun setupImagePager() {
        val viewPager: ViewPager2 = findViewById(R.id.viewpager_backdrop)
        thumbnailRecycler = findViewById(R.id.thumbnail_recycler)
        
        // 准备多张图片资源（使用所有可用的cheese图片）
        val imageResources = listOf(
            R.drawable.cheese_1,
            R.drawable.cheese_2,
            R.drawable.cheese_3,
            R.drawable.cheese_4,
            R.drawable.cheese_5
        )
        
        // 设置ViewPager2适配器
        val adapter = ImagePagerAdapter(imageResources)
        viewPager.adapter = adapter
        
        // 禁用ViewPager2的嵌套滚动，避免与CollapsingToolbarLayout冲突
        viewPager.isNestedScrollingEnabled = false
        
        // 设置缩略图RecyclerView
        thumbnailRecycler?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        thumbnailAdapter = ThumbnailAdapter(imageResources) { position ->
            // 点击缩略图时，切换到大图
            viewPager.setCurrentItem(position, true)
        }
        thumbnailRecycler?.adapter = thumbnailAdapter
        
        // 监听ViewPager2的页面切换，更新缩略图选中状态
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                thumbnailAdapter?.selectedPosition = position
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.sample_actions, menu)
        return false
    }

    companion object {
        const val EXTRA_NAME = "cheese_name"
    }
}