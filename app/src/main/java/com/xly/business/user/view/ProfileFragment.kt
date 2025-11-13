package com.xly.business.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.xly.base.LYBaseFragment
import com.xly.business.login.view.LoginActivity
import com.xly.business.user.viewmodel.ProfileViewModel
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.databinding.FragmentProfileBinding
import com.xly.middlelibrary.utils.click
import com.xly.middlelibrary.utils.MMKVManager

class ProfileFragment : LYBaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    private var headerBackground: View? = null
    private var initialHeaderHeight = 0
    private var maxScale = 1.5f // 最大放大倍数

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshLayout()
        loadUserProfile()
        loadProfileStats()
    }
    
    /**
     * 设置刷新布局和背景放大效果
     */
    private fun setupRefreshLayout() {
        headerBackground = viewBind.headerBackground
        initialHeaderHeight = headerBackground?.height ?: 280
        
        // 设置刷新头部
        viewBind.refreshLayout.setRefreshHeader(MaterialHeader(requireActivity()))
        viewBind.refreshLayout.setEnableRefresh(true)
        
        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            // 刷新数据
            loadUserProfile()
            loadProfileStats()
            refreshLayout.finishRefresh(1000) // 1秒后完成刷新
        }
        
        // 监听下拉进度，实现背景放大效果
        viewBind.refreshLayout.setOnMultiListener(object : com.scwang.smart.refresh.layout.listener.OnMultiListener {
            override fun onRefresh(refreshLayout: com.scwang.smart.refresh.layout.api.RefreshLayout) {
                // 刷新回调，已在setOnRefreshListener中处理
            }
            
            override fun onLoadMore(refreshLayout: com.scwang.smart.refresh.layout.api.RefreshLayout) {
                // 加载更多回调，此页面不需要加载更多功能
            }

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {

            }

            override fun onHeaderMoving(
                header: com.scwang.smart.refresh.layout.api.RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                // percent: 0-1，表示下拉进度
                // 实现阻尼弹性效果：当percent超过1时，缩放速度减慢
                val scale = if (percent <= 1f) {
                    1f + (percent * 0.3f) // 正常下拉时，最多放大30%
                } else {
                    // 超过正常范围时，使用阻尼效果
                    val excess = percent - 1f
                    1.3f + (excess * 0.2f).coerceAtMost(0.2f) // 最多再放大20%
                }
                
                headerBackground?.apply {
                    scaleX = scale
                    scaleY = scale
                    // 向下移动，营造弹性效果
                    translationY = offset * 0.3f
                }
            }
            
            override fun onHeaderReleased(
                header: com.scwang.smart.refresh.layout.api.RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                // 释放时，恢复背景大小
                headerBackground?.animate()
                    ?.scaleX(1f)
                    ?.scaleY(1f)
                    ?.translationY(0f)
                    ?.setDuration(300)
                    ?.setInterpolator(DecelerateInterpolator())
                    ?.start()
            }
            
            override fun onHeaderStartAnimator(
                header: com.scwang.smart.refresh.layout.api.RefreshHeader?,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                // 刷新动画开始
            }
            
            override fun onHeaderFinish(
                header: com.scwang.smart.refresh.layout.api.RefreshHeader?,
                success: Boolean
            ) {
                // 刷新完成，恢复背景
                headerBackground?.animate()
                    ?.scaleX(1f)
                    ?.scaleY(1f)
                    ?.translationY(0f)
                    ?.setDuration(300)
                    ?.setInterpolator(DecelerateInterpolator())
                    ?.start()
            }

            override fun onFooterMoving(
                footer: RefreshFooter?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                footerHeight: Int,
                maxDragHeight: Int
            ) {

            }

            override fun onFooterReleased(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {

            }

            override fun onFooterStartAnimator(
                footer: RefreshFooter?,
                footerHeight: Int,
                maxDragHeight: Int
            ) {

            }

            override fun onFooterFinish(footer: RefreshFooter?, success: Boolean) {

            }
        })
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

        viewBind.vipLl.click {
            LookStarMeActivity.start(requireContext())
        }

        // 资料待完善按钮
        viewBind.profileCompleteButton.click {
            showToast("跳转到资料完善页面")
            // TODO: 跳转到资料完善页面
        }

        // 我的理想型按钮
        viewBind.idealTypeButton.click {
            showToast("跳转到理想型设置页面")
            // TODO: 跳转到理想型设置页面
        }

        // 我的相册
        viewBind.myAlbumItem.click {
            showToast("跳转到我的相册页面")
            // TODO: 跳转到相册页面
        }

        // 我的认证
        viewBind.myVerificationItem.click {
            showToast("跳转到认证页面")
            // TODO: 跳转到认证页面
        }

        // 我的钱包
        viewBind.myWalletItem.click {
            showToast("跳转到钱包页面")
            // TODO: 跳转到钱包页面
        }

        // 推荐给朋友
        viewBind.recommendFriendItem.click {
            showToast("分享应用给朋友")
            // TODO: 实现分享功能
        }

        // 搜索用户
        viewBind.searchUserItem.click {
            showToast("跳转到搜索用户页面")
            // TODO: 跳转到搜索页面
        }

        // 设置
        viewBind.settingsItem.click {
            AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("是否确认退出登录？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定") { _, _ ->
                    MMKVManager.remove(MMKVManager.KEY_AUTH_SUCCESS)
                    MMKVManager.remove(MMKVManager.KEY_LOGIN_SUCCESS)
                    LoginActivity.start(requireActivity())
                }
                .show()
        }

        // 立即上传按钮
        viewBind.uploadNowButton.click {
            showToast("跳转到上传页面")
            // TODO: 跳转到上传页面
        }

        // 视频播放按钮
        viewBind.videoPlayButton.click {
            showToast("播放视频")
            // TODO: 播放视频
        }
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
        // 更新统计数据
        viewBind.likesCountText.text = stats.likesCount.toString()
        viewBind.visitorsCountText.text = stats.visitorsCount.toString()
        viewBind.popularityText.text = "${stats.popularityScore}%"
        viewBind.interactionsText.text = stats.interactionsCount.toString()
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