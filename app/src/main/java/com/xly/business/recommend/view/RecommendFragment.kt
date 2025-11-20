package com.xly.business.recommend.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.jspp.model.UserCard
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.DialogFateUserCardBinding
import com.xly.databinding.FragmentRecommendBinding
import com.xly.middlelibrary.utils.LYFontUtil
import com.xly.middlelibrary.utils.click
import kotlin.random.Random

class RecommendFragment : LYBaseFragment<FragmentRecommendBinding, RecommendViewModel>() {

    private val fragments = listOf(
        RecommendContentFragment(),
        HometownFragment()
    )
    
    // 随缘按钮相关
    private var fateButtonAnimation: AnimatorSet? = null
    private var fateDialog: Dialog? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupFateButtonAnimation()
    }
    
    override fun onResume() {
        super.onResume()
        // 恢复动画
        startFateButtonAnimation()
    }
    
    override fun onPause() {
        super.onPause()
        // 暂停动画
        stopFateButtonAnimation()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        stopFateButtonAnimation()
        fateDialog?.dismiss()
        // 取消所有待执行的Handler任务
        handler.removeCallbacksAndMessages(null)
        fateDialog = null
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
                if (position == 0) android.R.color.white else R.color.text_primary
            )
        )
        viewBind.tabHometown.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (position == 1) android.R.color.white else R.color.text_primary
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
            // 获取Tab相对于容器的位置（使用getLocationOnScreen计算绝对位置，然后转换为相对位置）
            val tabLocation = IntArray(2)
            val containerLocation = IntArray(2)
            targetTab.getLocationOnScreen(tabLocation)
            container.getLocationOnScreen(containerLocation)
            
            // 计算Tab相对于容器的位置
            val targetLeft = (tabLocation[0] - containerLocation[0]).toFloat()
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
        
        // 随缘入口按钮点击
        viewBind.fateButtonContainer.setOnClickListener {
            startFateLoading()
        }
    }
    
    /**
     * 设置随缘按钮动画：放大缩小循环
     */
    private fun setupFateButtonAnimation() {
        // 放大动画
        val scaleUpX = ObjectAnimator.ofFloat(viewBind.fateButton, "scaleX", 1.0f, 1.2f).apply {
            duration = 600
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        val scaleUpY = ObjectAnimator.ofFloat(viewBind.fateButton, "scaleY", 1.0f, 1.2f).apply {
            duration = 600
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        
        // 组合动画：同时放大缩小X和Y
        fateButtonAnimation = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }
        
        startFateButtonAnimation()
    }

    /**
     * 启动随缘按钮动画
     */
    private fun startFateButtonAnimation() {
        fateButtonAnimation?.start()
    }

    /**
     * 停止随缘按钮动画
     */
    private fun stopFateButtonAnimation() {
        fateButtonAnimation?.cancel()
    }

    /**
     * 开始加载状态（模拟网络请求）
     */
    private fun startFateLoading() {
        // 停止按钮动画
        stopFateButtonAnimation()
        
        // 显示进度条，隐藏按钮图标
        viewBind.fateProgressBar.visibility = View.VISIBLE
        viewBind.fateButton.visibility = View.GONE
        
        // 禁用点击
        viewBind.fateButtonContainer.isClickable = false
        
        // 模拟网络请求延迟（1-2秒随机）
        val delay = (1000..2000).random().toLong()
        handler.postDelayed({
            // 加载完成，显示卡片
            showFateUserCard()
            // 隐藏进度条，恢复按钮图标
            hideFateLoading()
        }, delay)
    }
    
    /**
     * 隐藏加载状态
     */
    private fun hideFateLoading() {
        viewBind.fateProgressBar.visibility = View.GONE
        viewBind.fateButton.visibility = View.VISIBLE
        viewBind.fateButtonContainer.isClickable = true
        // 恢复按钮动画
        startFateButtonAnimation()
    }

    /**
     * 显示缘分用户卡片
     */
    private fun showFateUserCard() {
        // 生成随机缘分用户
        val fateUser = generateRandomFateUser()
        
        // 创建Dialog
        val dialogBinding = DialogFateUserCardBinding.inflate(layoutInflater)
        fateDialog = Dialog(requireContext(), R.style.FateCardDialogStyle).apply {
            setContentView(dialogBinding.root)
            setCancelable(true)
            setCanceledOnTouchOutside(false) // 点击外部不关闭
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            
            // 设置Dialog宽度为屏幕的80%（5分之4）
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val dialogWidth = (screenWidth * 0.8).toInt()
            window?.setLayout(
                dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // 绑定用户数据
        bindFateUserData(dialogBinding, fateUser)

        // 设置点击事件
        dialogBinding.ivClose.setOnClickListener {
            dismissFateDialog()
        }

        dialogBinding.tvLike.setOnClickListener {
            showToast("喜欢了 ${fateUser.name}")
            dismissFateDialog()
        }

        dialogBinding.tvDislike.setOnClickListener {
            dismissFateDialog()
        }

        // 显示Dialog并添加弹出动画
        fateDialog?.show()
        animateDialogShow(dialogBinding.root)
    }

    /**
     * 绑定缘分用户数据
     */
    private fun bindFateUserData(binding: DialogFateUserCardBinding, user: UserCard) {
        // 姓名和年龄合并显示，用逗号隔开
        binding.tvNameAge.text = "${user.name}，${user.age}岁"
        // 设置字体样式
        binding.tvNameAge.typeface = LYFontUtil.getMediumFont(requireContext())
        
        // 设置认证标识（在年龄后面）- 默认显示
        binding.verifyIv.visibility = View.VISIBLE
        
        // 设置家乡和现居地
        setupLocationTags(binding, user.hometown, user.residence)
        
        // 设置头像
        val avatarResId = resources.getIdentifier(
            "head_${Random.nextInt(1, 9)}",
            "mipmap",
            requireContext().packageName
        )
        if (avatarResId != 0) {
            binding.ivAvatar.setImageResource(avatarResId)
        }

        // 设置标签
        setupTags(binding.llTags, user.tags)
    }
    
    /**
     * 设置家乡和现居地标签
     */
    private fun setupLocationTags(binding: DialogFateUserCardBinding, hometown: String, residence: String) {
        // 设置家乡
        if (hometown.isNotEmpty()) {
            binding.llHometown.visibility = View.VISIBLE
            binding.tvHometown.text = hometown
            binding.tvHometownDot.visibility = View.VISIBLE
        } else {
            binding.llHometown.visibility = View.GONE
            binding.tvHometownDot.visibility = View.GONE
        }
        
        // 设置现居地
        if (residence.isNotEmpty()) {
            binding.llResidence.visibility = View.VISIBLE
            binding.tvResidence.text = residence
            binding.tvResidenceDot.visibility = View.VISIBLE
        } else {
            binding.llResidence.visibility = View.GONE
            binding.tvResidenceDot.visibility = View.GONE
        }
        
        // 如果家乡和现居地都为空，隐藏整个容器
        if (hometown.isEmpty() && residence.isEmpty()) {
            binding.llLocationContainer.visibility = View.GONE
        } else {
            binding.llLocationContainer.visibility = View.VISIBLE
        }
    }

    /**
     * 设置标签
     */
    private fun setupTags(tagContainer: LinearLayout, tags: List<String>) {
        tagContainer.removeAllViews()
        
        val displayTags = if (tags.isNotEmpty()) {
            tags.take(3)
        } else {
            listOf("旅行", "摄影", "音乐").shuffled().take(2)
        }

        if (displayTags.isNotEmpty()) {
            tagContainer.visibility = View.VISIBLE
            displayTags.forEach { tag ->
                val tagView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_tag, tagContainer, false)
                val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
                tvTag.text = tag
                // 设置橘色背景（用于随缘卡片）
                tvTag.background = ContextCompat.getDrawable(requireContext(), R.drawable.tag_background_orange)
                // 缩小标签：减小字体大小和padding
                tvTag.textSize = 10f
                tvTag.setPadding(
                    (8 * resources.displayMetrics.density).toInt(),
                    (3 * resources.displayMetrics.density).toInt(),
                    (8 * resources.displayMetrics.density).toInt(),
                    (3 * resources.displayMetrics.density).toInt()
                )
                // 减小标签之间的间距
                val layoutParams = tagView.layoutParams as? android.view.ViewGroup.MarginLayoutParams
                layoutParams?.marginEnd = (4 * resources.displayMetrics.density).toInt()
                layoutParams?.bottomMargin = (4 * resources.displayMetrics.density).toInt()
                tagContainer.addView(tagView)
            }
        } else {
            tagContainer.visibility = View.GONE
        }
    }

    /**
     * 生成随机缘分用户
     */
    private fun generateRandomFateUser(): UserCard {
        val names = listOf("小雨", "小晴", "小月", "小星", "小云", "小风", "小阳", "小梦")
        val locations = listOf("北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "西安")
        val bios = listOf(
            "喜欢旅行和摄影，寻找志同道合的人",
            "热爱生活，享受每一个美好瞬间",
            "期待遇见有趣的灵魂",
            "相信缘分，珍惜每一次相遇",
            "热爱音乐和阅读，寻找心灵的共鸣"
        )
        val allTags = listOf("旅行", "摄影", "音乐", "阅读", "运动", "美食", "电影", "艺术")
        val avatarResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )

        return UserCard(
            id = "fate_${System.currentTimeMillis()}",
            name = names.random(),
            age = Random.nextInt(22, 35),
            location = locations.random(),
            avatarUrl = avatarResources.random(),
            bio = bios.random(),
            tags = allTags.shuffled().take(Random.nextInt(2, 5)),
            photos = emptyList(),
            occupation = "",
            education = "",
            height = Random.nextInt(160, 175),
            weight = Random.nextInt(45, 65),
            isOnline = Random.nextBoolean(),
            distance = "${Random.nextInt(1, 50)}km",
            lastActiveTime = System.currentTimeMillis(),
            hometown = locations.random(),
            residence = locations.random()
        )
    }

    /**
     * 关闭缘分卡片Dialog
     */
    private fun dismissFateDialog() {
        fateDialog?.let { dialog ->
            animateDialogDismiss(dialog.window?.decorView?.findViewById(android.R.id.content)) {
                dialog.dismiss()
                fateDialog = null
            }
        }
    }

    /**
     * Dialog显示动画 - 弹性弹出效果（先放大再回归正常大小）
     */
    private fun animateDialogShow(view: View?) {
        view?.let {
            it.scaleX = 0.5f
            it.scaleY = 0.5f
            it.alpha = 0f
            
            val scaleX = ObjectAnimator.ofFloat(it, "scaleX", 0.5f, 1.1f, 1.0f).apply {
                duration = 400
                interpolator = OvershootInterpolator()
            }
            val scaleY = ObjectAnimator.ofFloat(it, "scaleY", 0.5f, 1.1f, 1.0f).apply {
                duration = 400
                interpolator = OvershootInterpolator()
            }
            val alpha = ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).apply {
                duration = 400
            }
            
            AnimatorSet().apply {
                playTogether(scaleX, scaleY, alpha)
                start()
            }
        }
    }

    /**
     * Dialog消失动画 - 缩小并淡出
     */
    private fun animateDialogDismiss(view: View?, onEnd: () -> Unit) {
        view?.let {
            val scaleX = ObjectAnimator.ofFloat(it, "scaleX", 1.0f, 0.8f).apply {
                duration = 200
            }
            val scaleY = ObjectAnimator.ofFloat(it, "scaleY", 1.0f, 0.8f).apply {
                duration = 200
            }
            val alpha = ObjectAnimator.ofFloat(it, "alpha", 1f, 0f).apply {
                duration = 200
            }
            
            AnimatorSet().apply {
                playTogether(scaleX, scaleY, alpha)
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        onEnd()
                    }
                })
                start()
            }
        } ?: onEnd()
    }

    private fun showFilterDialog() {
        FilterActivity.start(requireContext())
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
