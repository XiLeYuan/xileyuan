package com.xly.business.user.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jspp.model.UserCard
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.user.viewmodel.ProfileViewModel
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.databinding.DialogFateUserCardBinding
import com.xly.databinding.FragmentProfileBinding
import com.xly.middlelibrary.utils.LYFontUtil
import kotlin.random.Random

class ProfileFragment : LYBaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    private var fateButtonAnimation: AnimatorSet? = null
    private var fateDialog: Dialog? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshLayout()
        loadUserProfile()
        loadProfileStats()
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
    
    /**
     * 设置刷新布局
     */
    private fun setupRefreshLayout() {
        // 设置刷新头部
        viewBind.refreshLayout.setRefreshHeader(MaterialHeader(requireActivity()))
        viewBind.refreshLayout.setEnableRefresh(true)
        // 禁用过度滚动，避免与NestedScrollView冲突
        viewBind.refreshLayout.setEnableOverScrollDrag(false)
        viewBind.refreshLayout.setEnableOverScrollBounce(false)
        
        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            // 刷新数据
            loadUserProfile()
            loadProfileStats()
            refreshLayout.finishRefresh(1000) // 1秒后完成刷新
        }
    }

    override fun initObservers() {
        // 观察用户资料数据
        viewModel.userProfileLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { userInfo ->
                updateUserProfile(userInfo)
            }.onFailure { error ->

            }
        })

        // 观察统计数据
        viewModel.profileStatsLiveData.observe(viewLifecycleOwner, Observer { stats ->
            updateProfileStats(stats)
        })
    }

    override fun initView() {
        // 初始化界面元素
        setupUI()
    }

    override fun initOnClick() {
        // 设置点击事件
        setupClickListeners()
    }

    private fun setupUI() {
        // 设置头像（这里使用默认头像，实际项目中应该从用户资料中获取）
        // viewBind.avatarImage 已在布局中设置默认头像
        
        // 设置昵称（这里使用默认昵称，实际项目中应该从用户资料中获取）
        viewBind.nicknameText.text = "Ella"
        // 设置用户名字体
        viewBind.nicknameText.typeface = LYFontUtil.getMediumFont(requireContext())
        
        // 设置VIP标识显示（根据用户VIP状态）
        viewBind.ivVip.visibility = View.VISIBLE
        
        // 设置认证标识显示（根据用户认证状态）
        viewBind.ivVerified.visibility = View.VISIBLE
        
        // 设置统计数字字体
        viewBind.tvLikedPeopleCount.typeface = LYFontUtil.getMediumFont(requireContext())
        viewBind.tvVisitorsCount.typeface = LYFontUtil.getMediumFont(requireContext())
        viewBind.tvWhoLikedMeCount.typeface = LYFontUtil.getMediumFont(requireContext())
        
        // 设置角标（示例数据，实际应该从服务器获取）
        updateBadges(5, 3, 8) // 喜欢的人新增5，访客新增3，谁喜欢我新增8
    }
    
    /**
     * 更新角标显示
     * @param likedPeopleNewCount 喜欢的人新增数量
     * @param visitorsNewCount 访客新增数量
     * @param whoLikedMeNewCount 谁喜欢我新增数量
     */
    private fun updateBadges(likedPeopleNewCount: Int, visitorsNewCount: Int, whoLikedMeNewCount: Int) {
        // 更新"喜欢的人"角标
        updateBadge(viewBind.tvLikedPeopleBadge, likedPeopleNewCount)
        
        // 更新"访客"角标
        updateBadge(viewBind.tvVisitorsBadge, visitorsNewCount)
        
        // 更新"谁喜欢我"角标
        updateBadge(viewBind.tvWhoLikedMeBadge, whoLikedMeNewCount)
    }
    
    /**
     * 更新单个角标
     */
    private fun updateBadge(badgeView: TextView, newCount: Int) {
        if (newCount > 0) {
            badgeView.visibility = View.VISIBLE
            // 如果数量大于99，显示"99+"
            badgeView.text = if (newCount > 99) "99+" else newCount.toString()
        } else {
            badgeView.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {

        // VIP卡片内层点击 - 直接开通VIP
        viewBind.llVipInner.setOnClickListener {
            showToast("开通VIP")
        }

        // VIP开通按钮点击
        viewBind.tvVipButton.setOnClickListener {
            showToast("开通VIP")
        }

        // 随缘入口按钮点击
        viewBind.fateButtonContainer.setOnClickListener {
            startFateLoading()
        }

        // 我的资料入口点击
        viewBind.llMyProfile.setOnClickListener {
            showToast("我的资料")
        }

        // 我的特权点击
        viewBind.llMyPrivilege.setOnClickListener {
            showToast("我的特权")
        }

        // 我的钱包点击
        viewBind.llMyWallet.setOnClickListener {
            showToast("我的钱包")
        }

        // 我的认证点击
        viewBind.llMyVerification.setOnClickListener {
            VerificationActivity.start(requireContext())
        }

        // 我的缘份点击
        viewBind.llMyFate.setOnClickListener {
            showToast("我的缘份")
        }

        // 设置点击
        viewBind.llSettings.setOnClickListener {
            showToast("设置")
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

        return UserCard(
            id = "fate_${System.currentTimeMillis()}",
            name = names.random(),
            age = Random.nextInt(22, 35),
            location = locations.random(),
            avatarUrl = "",
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
        view?.apply {
            alpha = 0f
            scaleX = 0.3f
            scaleY = 0.3f

            // 使用弹性动画：先放大到1.15倍，再回到正常大小
            animate()
                .alpha(1f)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(200)
                .withEndAction {
                    // 回归正常大小，使用弹性插值器
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(1.5f))
                        .start()
                }
                .start()
        }
    }

    /**
     * Dialog关闭动画
     */
    private fun animateDialogDismiss(view: View?, onEnd: () -> Unit) {
        view?.apply {
            animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .translationY(100f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction(onEnd)
                .start()
        } ?: onEnd()
    }

    private fun loadUserProfile() {
        // 加载用户资料，这里使用模拟的用户ID
        viewModel.loadUserProfile("current_user_id")
    }

    private fun loadProfileStats() {
        // 加载统计数据，这里使用模拟的用户ID
        viewModel.loadProfileStats("current_user_id")
    }

    private fun updateUserProfile(userInfo: com.xly.business.user.UserInfo) {
        // 更新用户资料显示
        viewBind.nicknameText.text = userInfo.name

    }

    private fun updateProfileStats(stats: ProfileViewModel.ProfileStats) {
        // 更新统计数据
        viewBind.tvLikedPeopleCount.text = stats.likedPeopleCount.toString()
        viewBind.tvVisitorsCount.text = stats.visitorsCount.toString()
        viewBind.tvWhoLikedMeCount.text = stats.whoLikedMeCount.toString()
        
        // 更新角标（从统计数据中获取新增数量）
        updateBadges(
            stats.likedPeopleNewCount,
            stats.visitorsNewCount,
            stats.whoLikedMeNewCount
        )
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): ProfileViewModel {
        return ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
    }
}