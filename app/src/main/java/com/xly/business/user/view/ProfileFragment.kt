package com.xly.business.user.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexboxLayout
import com.jspp.model.UserCard
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.user.viewmodel.ProfileViewModel
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.databinding.DialogFateUserCardBinding
import com.xly.databinding.FragmentProfileBinding
import kotlin.random.Random

class ProfileFragment : LYBaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    private var fateButtonAnimation: AnimatorSet? = null
    private var fateDialog: Dialog? = null

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
        
        // 设置VIP标识显示（根据用户VIP状态）
        viewBind.ivVip.visibility = View.VISIBLE // TODO: 根据用户VIP状态显示/隐藏
        
        // 设置认证标识显示（根据用户认证状态）
        viewBind.ivVerified.visibility = View.VISIBLE // TODO: 根据用户认证状态显示/隐藏
    }

    private fun setupClickListeners() {

        // VIP卡片内层点击 - 直接开通VIP
        viewBind.llVipInner.setOnClickListener {
            // TODO: 直接开通VIP的逻辑
            showToast("开通VIP")
        }

        // VIP开通按钮点击
        viewBind.tvVipButton.setOnClickListener {
            // TODO: 直接开通VIP的逻辑
            showToast("开通VIP")
        }

        // 随缘入口按钮点击
        viewBind.fateButton.setOnClickListener {
            showFateUserCard()
        }

        // 我的资料入口点击
        viewBind.llMyProfile.setOnClickListener {
            // TODO: 跳转到我的资料页面
            showToast("我的资料")
        }

        // 我的特权点击
        viewBind.llMyPrivilege.setOnClickListener {
            // TODO: 跳转到我的特权页面
            showToast("我的特权")
        }

        // 我的钱包点击
        viewBind.llMyWallet.setOnClickListener {
            // TODO: 跳转到我的钱包页面
            showToast("我的钱包")
        }

        // 我的认证点击
        viewBind.llMyVerification.setOnClickListener {
            // TODO: 跳转到我的认证页面
            showToast("我的认证")
        }

        // 我的缘份点击
        viewBind.llMyFate.setOnClickListener {
            // TODO: 跳转到我的缘份页面
            showToast("我的缘份")
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
            setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        // 绑定用户数据
        bindFateUserData(dialogBinding, fateUser)

        // 设置点击事件
        dialogBinding.ivClose.setOnClickListener {
            dismissFateDialog()
        }

        dialogBinding.tvLike.setOnClickListener {
            // TODO: 处理喜欢逻辑
            showToast("喜欢了 ${fateUser.name}")
            dismissFateDialog()
        }

        dialogBinding.tvDislike.setOnClickListener {
            // TODO: 处理不喜欢逻辑
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
        binding.tvName.text = user.name
        binding.tvAge.text = "${user.age}岁"
        binding.tvBio.text = user.bio.ifEmpty { "这是一个缘分用户，期待与你相遇" }
        
        // 设置头像
        val avatarResId = resources.getIdentifier(
            "head_${Random.nextInt(1, 9)}",
            "mipmap",
            requireContext().packageName
        )
        if (avatarResId != 0) {
            binding.ivAvatar.setImageResource(avatarResId)
        }

        // 设置背景
        val bgResId = resources.getIdentifier(
            "find_img_${Random.nextInt(1, 4)}",
            "mipmap",
            requireContext().packageName
        )
        if (bgResId != 0) {
            binding.ivBackground.setImageResource(bgResId)
        }

        // 设置标签
        setupTags(binding.tagContainer, user.tags)

        // 设置认证标识
        binding.verifyIv.visibility = if (Random.nextBoolean()) View.VISIBLE else View.GONE
    }

    /**
     * 设置标签
     */
    private fun setupTags(tagContainer: FlexboxLayout, tags: List<String>) {
        tagContainer.removeAllViews()
        
        val displayTags = if (tags.isNotEmpty()) {
            tags.take(3)
        } else {
            listOf("旅行", "摄影", "音乐").shuffled().take(2)
        }

        displayTags.forEach { tag ->
            val tagView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_tag, tagContainer, false)
            val tvTag = tagView.findViewById<TextView>(R.id.tvTag)
            tvTag.text = tag
            tagContainer.addView(tagView)
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
            lastActiveTime = System.currentTimeMillis()
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
     * Dialog显示动画
     */
    private fun animateDialogShow(view: View?) {
        view?.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            translationY = 100f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
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
        // TODO: 更新头像等其他用户信息
        // TODO: 根据userInfo.isVip显示/隐藏VIP标识
        // TODO: 根据userInfo.isVerified显示/隐藏认证标识
    }

    private fun updateProfileStats(stats: ProfileViewModel.ProfileStats) {

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