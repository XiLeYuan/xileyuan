package com.xly.business.login.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.view.adapter.UserInfoPagerAdapter
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoBinding

class UserInfoActivity : LYBaseActivity<ActivityUserInfoBinding, LoginViewModel>(), UserInfoStepFragment.OnInputValidListener {
    private val totalSteps = 17
    private lateinit var pagerAdapter: UserInfoPagerAdapter
    private var currentStep = 0

    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityUserInfoBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = UserInfoPagerAdapter(this, totalSteps, this)
        viewBind.viewPager.adapter = pagerAdapter
        viewBind.viewPager.isUserInputEnabled = false // 禁止滑动
        updateProgress(0)
        updateButtonState(true)
        viewBind.btnBack.setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                viewBind.viewPager.setCurrentItem(currentStep, true)
                updateProgress(currentStep)
                updateButtonState(pagerAdapter.isStepValid(currentStep))
            } else {
                finish()
            }
        }
        viewBind.btnNext.setOnClickListener {
            if (currentStep < totalSteps - 1) {
                currentStep++
                viewBind.viewPager.setCurrentItem(currentStep, true)
                updateProgress(currentStep)
                updateButtonState(pagerAdapter.isStepValid(currentStep))
            } else {
                // TODO: 提交所有信息
            }
        }
    }

    private fun updateProgress(step: Int) {
        viewBind.progressBar.progress = step + 1
    }

    private fun updateButtonState(valid: Boolean) {
        viewBind.btnNext.isEnabled = valid
        viewBind.btnNext.alpha = if (valid) 1f else 0.5f
    }

    // Fragment回调，输入有效性变化时调用
    override fun onInputValid(step: Int, valid: Boolean) {
        if (step == currentStep) {
            updateButtonState(valid)
        }
        pagerAdapter.setStepValid(step, valid)
    }
}
