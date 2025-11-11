package com.xly.business.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.login.view.LoginActivity
import com.xly.business.user.viewmodel.ProfileViewModel
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.databinding.FragmentProfileBinding
import com.xly.middlelibrary.utils.click
import com.xly.middlelibrary.utils.MMKVManager

class ProfileFragment : LYBaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
        loadProfileStats()
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
//        viewBind.avatarImage.setImageResource(com.xly.R.mipmap.ic_launcher_foreground)
        
        // 设置昵称（这里使用默认昵称，实际项目中应该从用户资料中获取）
        viewBind.nicknameText.text = "Ella"
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