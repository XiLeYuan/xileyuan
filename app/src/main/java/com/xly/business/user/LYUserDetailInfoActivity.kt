package com.xly.business.user




import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.jspp.model.UserCard
import com.xly.R
import com.xly.business.user.adapter.UserDetailAdapter
import com.xly.business.user.adapter.UserImagePagerAdapter
import com.xly.databinding.ActivityUserDetailBinding
import com.xly.databinding.ActivityUserDetailInfoBinding
import kotlin.math.abs

class LYUserDetailInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailInfoBinding
    private lateinit var imagePagerAdapter: UserImagePagerAdapter
    private lateinit var detailAdapter: UserDetailAdapter

    private var userName: String = ""
    private var userImages: List<String> = emptyList()
    private var imageContainerHeight = 0
    private var maxImageHeight = 0

    companion object {
        const val EXTRA_USER_CARD = "user_card"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置状态栏透明
        setupStatusBar()

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

    private fun setupStatusBar() {
        // 设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // 设置状态栏文字为白色
        /*ViewCompat.getWindowInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = false
        }*/
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
        binding.tvUserName.text = userName
        binding.tvToolbarUserName.text = userName

        // 设置 ViewPager2
        imagePagerAdapter = UserImagePagerAdapter(userImages)
        binding.viewPagerImages.adapter = imagePagerAdapter

        // 设置图片指示器
        setupIndicators(userImages.size)

        // 设置详细信息列表
        binding.recyclerViewDetails.layoutManager = LinearLayoutManager(this)
        detailAdapter = UserDetailAdapter(generateDetailItems())
        binding.recyclerViewDetails.adapter = detailAdapter

        // 设置返回按钮
        binding.ibBack.setOnClickListener {
            onBackPressed()
        }

        // 获取图片容器高度
        binding.imageContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.imageContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    imageContainerHeight = binding.imageContainer.height
                    maxImageHeight = (resources.displayMetrics.heightPixels * 0.6f).toInt()
                }
            }
        )
    }

    private fun setupListeners() {
        // 监听 AppBarLayout 滚动（下拉放大效果）
        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            handleAppBarScroll(verticalOffset, appBarLayout.totalScrollRange)
        }

        // 监听 NestedScrollView 滚动（状态栏渐变）
        binding.nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            handleScrollViewScroll()
        }
    }

    /**
     * 处理 AppBarLayout 滚动（下拉放大效果）
     */
    private fun handleAppBarScroll(verticalOffset: Int, totalScrollRange: Int) {
        if (totalScrollRange == 0) return

        // 计算滚动比例
        val scrollRatio = abs(verticalOffset).toFloat() / totalScrollRange

        // 下拉时放大图片
        if (verticalOffset >= 0) {
            // 向下滚动，放大图片
            val scale = 1f + (scrollRatio * 0.5f)  // 最大放大到 1.5 倍
            binding.viewPagerImages.scaleX = scale
            binding.viewPagerImages.scaleY = scale
        } else {
            // 向上滚动，恢复原始大小
            val scale = 1f - (scrollRatio * 0.3f)  // 可以稍微缩小
            binding.viewPagerImages.scaleX = scale.coerceAtLeast(0.7f)
            binding.viewPagerImages.scaleY = scale.coerceAtLeast(0.7f)
        }

        // 更新 Toolbar 背景透明度
        updateToolbarBackground(scrollRatio)
    }

    /**
     * 处理 ScrollView 滚动（状态栏渐变）
     */
    private fun handleScrollViewScroll() {
        val scrollY = binding.nestedScrollView.scrollY
        val imageHeight = imageContainerHeight

        // 当用户信息滚动到顶部时，显示用户名和白色背景
        if (scrollY >= imageHeight - 100) {
            showToolbarTitle(true)
            binding.customToolbar.setBackgroundColor(Color.WHITE)
        } else {
            val ratio = (scrollY.toFloat() / imageHeight).coerceIn(0f, 1f)
            showToolbarTitle(ratio > 0.5f)

            // 背景从透明渐变到白色
            val alpha = (ratio * 255).toInt().coerceIn(0, 255)
            binding.customToolbar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))
        }
    }

    /**
     * 更新 Toolbar 背景透明度
     */
    private fun updateToolbarBackground(scrollRatio: Float) {
        val alpha = (scrollRatio * 255).toInt().coerceIn(0, 255)
        binding.customToolbar.setBackgroundColor(Color.argb(alpha, 255, 255, 255))

        // 当滚动到一定程度时显示用户名
        if (scrollRatio > 0.3f) {
            showToolbarTitle(true)
            // 更新状态栏文字颜色为深色
           /* ViewCompat.getWindowInsetsController(window, window.decorView)?.apply {
                isAppearanceLightStatusBars = true
            }*/
        } else {
            showToolbarTitle(false)
            // 更新状态栏文字颜色为浅色
            /*ViewCompat.getWindowInsetsController(window, window.decorView)?.apply {
                isAppearanceLightStatusBars = false
            }*/
        }
    }

    /**
     * 显示/隐藏 Toolbar 中的用户名
     */
    private fun showToolbarTitle(show: Boolean) {
        binding.tvToolbarUserName.animate()
            .alpha(if (show) 1f else 0f)
            .setDuration(200)
            .start()

        binding.tvToolbarUserName.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * 设置图片指示器
     */
    private fun setupIndicators(count: Int) {
        binding.indicatorContainer.removeAllViews()

        if (count <= 1) {
            binding.indicatorContainer.visibility = View.GONE
            return
        }

        binding.indicatorContainer.visibility = View.VISIBLE

        for (i in 0 until count) {
            val indicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    marginEnd = if (i < count - 1) 8.dpToPx() else 0
                }
                setBackgroundResource(R.mipmap.head_img)
                background.setTint(
                    ContextCompat.getColor(
                        this@LYUserDetailInfoActivity,
                        if (i == 0) R.color.text_white else R.color.text_hint
                    )
                )
            }
            binding.indicatorContainer.addView(indicator)
        }

        // 监听 ViewPager2 切换
        binding.viewPagerImages.registerOnPageChangeCallback(
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
        for (i in 0 until binding.indicatorContainer.childCount) {
            val indicator = binding.indicatorContainer.getChildAt(i)
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