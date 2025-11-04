package com.xly.business.user








import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.jspp.model.UserCard
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.user.adapter.UserDetailAdapter
import com.xly.business.user.adapter.UserImagePagerAdapter
import com.xly.databinding.ActivityMainBinding
import com.xly.databinding.ActivityUserDetailInfoBinding
import com.xly.index.viewmodel.MainViewModel
import kotlin.math.abs

/**
 * 用户详情页 - 微信朋友圈风格
 * 实现效果：
 * 1. 顶部生活照支持左右滑动（ViewPager2）
 * 2. 用户信息卡片覆盖在图片上，白色圆角背景
 * 3. 向上滑动时图片跟随折叠（CollapsingToolbarLayout）
 * 4. 向下滑动时，图片完全显示后继续下拉有阻尼放大效果（类似朋友圈）
 */
class LYUserDetailInfoActivity : LYBaseActivity<ActivityUserDetailInfoBinding,RecommendViewModel>() {

    private lateinit var imagePagerAdapter: UserImagePagerAdapter
    private lateinit var detailAdapter: UserDetailAdapter

    private var userName: String = ""
    private var userImages: List<String> = emptyList()
    private var originalImageHeight = 0
    private var maxImageHeight = 0

    // 下拉放大相关
    private var lastY = 0f
    private var currentScale = 1f
    private var canPullDown = false
    private var isAppBarExpanded = true

    companion object {
        const val EXTRA_USER_CARD = "user_card"

        fun start(context: android.content.Context, userCard: UserCard, sharedView: View? = null) {
            val intent = android.content.Intent(context, LYUserDetailInfoActivity::class.java).apply {
                putExtra(EXTRA_USER_CARD, userCard)
            }

            if (sharedView != null && context is android.app.Activity) {
                val options = android.app.ActivityOptions.makeSceneTransitionAnimation(
                    context,
                    sharedView,
                    "user_card"
                )
                context.startActivity(intent, options.toBundle())
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 设置状态栏透明
//        setupStatusBar()

        // 获取用户数据
        val userCard = intent.getParcelableExtra<UserCard>(EXTRA_USER_CARD)
        if (userCard != null) {
            initUserData(userCard)
        }

        // 初始化视图
        initViews()

        // 设置监听
        setupListeners()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityUserDetailInfoBinding {
        return ActivityUserDetailInfoBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(this)[RecommendViewModel::class.java]
    }

    private fun setupStatusBar() {
        // 设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // 设置状态栏文字为白色
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            isAppearanceLightStatusBars = false
        }



    }

    private fun initUserData(userCard: UserCard) {
        userName = userCard.name

        // 模拟用户生活照数据
        userImages = listOf(
            userCard.avatarUrl,
            "https://example.com/user/life1.jpg",
            "https://example.com/user/life2.jpg",
            "https://example.com/user/life3.jpg"
        )
    }

    private fun initViews() {
        // 设置用户名
        viewBind.tvUserName.text = userName
        viewBind.tvToolbarUserName.text = userName

        // 设置 ViewPager2
        imagePagerAdapter = UserImagePagerAdapter(userImages)
        viewBind.viewPagerImages.adapter = imagePagerAdapter

        // 设置图片指示器
        setupIndicators(userImages.size)

        // 设置详细信息列表
        viewBind.recyclerViewDetails.layoutManager = LinearLayoutManager(this)
        detailAdapter = UserDetailAdapter(generateDetailItems())
        viewBind.recyclerViewDetails.adapter = detailAdapter

        // 设置返回按钮
        viewBind.ibBack.setOnClickListener {
            onBackPressed()
        }

        // 获取图片容器原始高度
        /*viewBind.imageContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewBind.imageContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    originalImageHeight = viewBind.imageContainer.height
                    maxImageHeight = (resources.displayMetrics.heightPixels * 0.7f).toInt()
                }
            }
        )*/
    }

    private fun setupListeners() {
        // 监听 AppBarLayout 滚动（向上滚动时折叠效果）
        viewBind.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            handleAppBarScroll(verticalOffset, appBarLayout.totalScrollRange)
        }

        // 监听 NestedScrollView 滚动（状态栏渐变）
        viewBind.nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            handleScrollViewScroll()
        }

        // 监听下拉手势（实现朋友圈式的下拉放大效果）
        setupPullDownListener()
    }

    /**
     * 设置下拉监听（实现朋友圈式的下拉放大效果）
     * 关键：只有在 AppBarLayout 完全展开且 NestedScrollView 在顶部时才能下拉
     */
    private fun setupPullDownListener() {
        // 监听 AppBarLayout 的展开状态
        viewBind.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            // AppBarLayout 完全展开时 verticalOffset == 0
            isAppBarExpanded = (verticalOffset == 0)

            // 如果正在滚动折叠，恢复图片缩放
            if (!isAppBarExpanded && currentScale > 1f) {
                resetImageScale()
            }
        }

        // 监听 NestedScrollView 的触摸事件
        viewBind.nestedScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastY = event.y
                    // 检查是否可以下拉：AppBarLayout 完全展开 且 NestedScrollView 在顶部
                    canPullDown = isAppBarExpanded &&
                            viewBind.nestedScrollView.scrollY == 0 &&
                            viewBind.appBarLayout.totalScrollRange == 0
                }
                MotionEvent.ACTION_MOVE -> {
                    if (canPullDown && isAppBarExpanded && viewBind.nestedScrollView.scrollY == 0) {
                        val deltaY = event.y - lastY
                        // 只有向下拉时才放大
                        if (deltaY > 0) {
                            handlePullDown(deltaY)
                            // 消费事件，阻止默认滚动
                            return@setOnTouchListener true
                        } else {
                            // 向上滑动，恢复缩放
                            if (currentScale > 1f) {
                                resetImageScale()
                            }
                        }
                    } else {
                        // 非下拉状态，恢复缩放
                        if (currentScale > 1f) {
                            resetImageScale()
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (canPullDown && currentScale > 1f) {
                        // 松手后恢复原始大小（带动画）
                        resetImageScaleWithAnimation()
                    }
                    canPullDown = false
                }
            }
            // 返回 false，让 NestedScrollView 继续处理正常的滚动事件
            false
        }
    }

    /**
     * 处理下拉放大
     * @param deltaY 下拉距离（像素）
     * 使用类似微信朋友圈的阻尼曲线
     */
    private fun handlePullDown(deltaY: Float) {
        // 计算缩放比例（使用阻尼效果，最大值限制在 1.5 倍）
        val maxPullDistance = originalImageHeight * 0.6f // 最大下拉距离为图片高度的 60%
        val pullRatio = (deltaY / maxPullDistance).coerceIn(0f, 1f)

        // 使用阻尼曲线（类似微信朋友圈的效果）
        // 使用 ease-out cubic 曲线：f(x) = 1 - (1-x)^3
        // 提供更自然的阻尼感，下拉越远阻力越大
        val oneMinusRatio = 1f - pullRatio
        val dampRatio = 1f - oneMinusRatio * oneMinusRatio * oneMinusRatio
        currentScale = 1f + dampRatio * 0.4f // 最大放大到 1.4 倍（更温和，类似朋友圈）

        // 应用缩放（中心缩放）
        viewBind.viewPagerImages.scaleX = currentScale
        viewBind.viewPagerImages.scaleY = currentScale
        viewBind.viewPagerImages.pivotX = viewBind.viewPagerImages.width / 2f
        viewBind.viewPagerImages.pivotY = viewBind.viewPagerImages.height / 2f

        // 同时增加容器高度（增强视觉效果）
        val newHeight = (originalImageHeight * currentScale).toInt()
        val params = viewBind.imageContainer.layoutParams
        if (params.height != newHeight && newHeight <= maxImageHeight) {
            params.height = newHeight
            viewBind.imageContainer.layoutParams = params
        }
    }

    /**
     * 重置图片缩放（无动画）
     */
    private fun resetImageScale() {
        if (currentScale != 1f) {
            currentScale = 1f
            viewBind.viewPagerImages.scaleX = 1f
            viewBind.viewPagerImages.scaleY = 1f

            val params = viewBind.imageContainer.layoutParams
            params.height = originalImageHeight
            viewBind.imageContainer.layoutParams = params
        }
    }

    /**
     * 重置图片缩放（带动画）
     */
    private fun resetImageScaleWithAnimation() {
        if (currentScale != 1f) {
            // 使用更流畅的动画曲线
            viewBind.viewPagerImages.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .start()

            // 恢复容器高度（使用 ValueAnimator 实现更流畅的动画）
            val startHeight = viewBind.imageContainer.height
            val endHeight = originalImageHeight
            android.animation.ValueAnimator.ofInt(startHeight, endHeight).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                addUpdateListener { animator ->
                    val params = viewBind.imageContainer.layoutParams
                    params.height = animator.animatedValue as Int
                    viewBind.imageContainer.layoutParams = params
                }
                start()
            }

            currentScale = 1f
        }
    }

    /**
     * 处理 AppBarLayout 滚动（向上滚动时折叠效果）
     * 使用 CollapsingToolbarLayout 的 parallax 效果
     */
    private fun handleAppBarScroll(verticalOffset: Int, totalScrollRange: Int) {
        if (totalScrollRange == 0) return

        // 向上滚动时，图片跟随折叠（通过 CollapsingToolbarLayout 的 parallax 实现）
        // 这里只需要更新 Toolbar 背景
        val scrollRatio = abs(verticalOffset).toFloat() / totalScrollRange

        // 更新 Toolbar 背景和状态栏
        updateToolbarBackground(scrollRatio)

        // 如果正在向上滚动，确保图片没有缩放
        if (verticalOffset < 0 && currentScale > 1f) {
            resetImageScale()
        }
    }

    /**
     * 处理 ScrollView 滚动（状态栏渐变）
     */
    private fun handleScrollViewScroll() {
        val scrollY = viewBind.nestedScrollView.scrollY
        val imageHeight = originalImageHeight

        // 当用户信息滚动到顶部时，显示用户名和白色背景
        if (scrollY >= imageHeight - 100) {
            showToolbarTitle(true)
            viewBind.customToolbar.setBackgroundColor(Color.WHITE)
        } else {
            val ratio = (scrollY.toFloat() / imageHeight).coerceIn(0f, 1f)
            showToolbarTitle(ratio > 0.5f)

            // 背景从透明渐变到白色
            val alpha = (ratio * 255).toInt().coerceIn(0, 255)
            viewBind.customToolbar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
        }
    }

    /**
     * 更新 Toolbar 背景透明度
     */
    private fun updateToolbarBackground(scrollRatio: Float) {
        val alpha = (scrollRatio * 255).toInt().coerceIn(0, 255)
        viewBind.customToolbar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))

        // 当滚动到一定程度时显示用户名
        if (scrollRatio > 0.3f) {
            showToolbarTitle(true)
            // 更新状态栏文字颜色为深色


            /*ViewCompat.getWindowInsetsController(window.decorView)?.apply {
                isAppearanceLightStatusBars = true
            }*/

        } else {
            showToolbarTitle(false)
            // 更新状态栏文字颜色为浅色


            /*ViewCompat.getWindowInsetsController(window.decorView)?.apply {
                isAppearanceLightStatusBars = false
            }*/
        }
    }

    /**
     * 显示/隐藏 Toolbar 中的用户名
     */
    private fun showToolbarTitle(show: Boolean) {
        /*viewBind.tvToolbarUserName.animate()
            .alpha(if (show) 1f else 0f)
            .setDuration(200)
            .start()

        viewBind.tvToolbarUserName.visibility = if (show) View.VISIBLE else View.GONE*/
    }

    /**
     * 设置图片指示器
     */
    private fun setupIndicators(count: Int) {
        viewBind.indicatorContainer.removeAllViews()

        if (count <= 1) {
            viewBind.indicatorContainer.visibility = View.GONE
            return
        }

        viewBind.indicatorContainer.visibility = View.VISIBLE

        for (i in 0 until count) {
            val indicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    marginEnd = if (i < count - 1) 8.dpToPx() else 0
                }
                setBackgroundResource(R.mipmap.arrow_right_icon)
                background.setTint(
                    ContextCompat.getColor(
                        this@LYUserDetailInfoActivity,
                        if (i == 0) R.color.text_white else R.color.text_hint
                    )
                )
            }
            viewBind.indicatorContainer.addView(indicator)
        }

        // 监听 ViewPager2 切换
        viewBind.viewPagerImages.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateIndicators(position)
                }
            }
        )
    }

    /**
     * 更新指示器状态
     */
    private fun updateIndicators(selectedPosition: Int) {
        for (i in 0 until viewBind.indicatorContainer.childCount) {
            val indicator = viewBind.indicatorContainer.getChildAt(i)
            indicator.background.setTint(
                ContextCompat.getColor(
                    this,
                    if (i == selectedPosition) R.color.text_white else R.color.text_hint
                )
            )
        }
    }

    /**
     * 生成详细信息列表数据
     */
    private fun generateDetailItems(): List<String> {
        return listOf(
            "基本信息",
            "兴趣爱好",
            "理想对象",
            "生活状态",
            "联系方式"
        )
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}


