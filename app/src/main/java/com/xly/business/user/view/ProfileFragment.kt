package com.xly.business.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.base.LYBaseFragment
import com.xly.business.user.viewmodel.ProfileViewModel
import com.xly.business.vip.view.LookStarMeActivity
import com.xly.databinding.FragmentProfileBinding

class ProfileFragment : LYBaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshLayout()
        loadUserProfile()
        loadProfileStats()
    }
    
    /**
     * 设置刷新布局
     */
    private fun setupRefreshLayout() {
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
        // VIP卡片外层点击 - 进入VIP页面
        viewBind.outVipRl.setOnClickListener {
            LookStarMeActivity.start(requireContext())
        }

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