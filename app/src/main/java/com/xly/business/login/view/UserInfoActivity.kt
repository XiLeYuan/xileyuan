package com.xly.business.login.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.view.adapter.UserInfoPagerAdapter
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoBinding
import com.xly.index.LYMainActivity
import com.xly.middlelibrary.utils.visible

class UserInfoActivity : LYBaseActivity<ActivityUserInfoBinding, LoginViewModel>(), UserInfoStepFragment.OnInputValidListener {
    private val totalSteps = 16
    private lateinit var pagerAdapter: UserInfoPagerAdapter
    private var currentStep = 0
    private var phoneNum = ""


    companion object {
        fun start(c: Context) {
            val intent = Intent(c, UserInfoActivity::class.java)
            (c as? Activity)?.startActivity(intent)
            (c as? Activity)?.finish()
        }
    }

    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityUserInfoBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = UserInfoPagerAdapter(this, totalSteps)
        viewBind.viewPager.adapter = pagerAdapter
        viewBind.viewPager.isUserInputEnabled = false // 禁止滑动
        updateProgress(0)
        updateButtonState(false) // 默认禁用下一步按钮
        viewBind.btnSkip.setOnClickListener {
            LYMainActivity.start(this)
            finish()
        }
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
                requestRegisterUserInfo()

            } else {
                // TODO: 提交所有信息
                LYMainActivity.start(this)
            }
        }
    }

    private fun updateProgress(step: Int) {
        viewBind.progressBar.progress = step + 1
        updateSkipButtonVisibility(step)
    }

    private fun updateButtonState(valid: Boolean) {
        // 在实名认证页面（最后一步）隐藏下一步按钮
        if (currentStep == 15) {
            viewBind.btnNext.visibility = android.view.View.GONE
            viewBind.btnBack.visibility = android.view.View.GONE
            viewBind.tvTip.visibility = android.view.View.GONE
        } else {
            viewBind.btnNext.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = valid
            viewBind.btnNext.alpha = if (valid) 1f else 0.5f
        }
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            // 处理登录成功
            hideLoading()
            Log.i("registerResult","registerResult==success")
        }
    }


    fun requestRegisterUserInfo() {
        val request = UserInfoRegisterReq().apply {
            step = 1
            phoneNumber = "18221547860"
            gender = "1"
        }
        viewModel.userInfoRegister(request)
    }


    fun goToStep(step: Int) {
        if (step in 0 until totalSteps) {
            currentStep = step
            viewBind.viewPager.setCurrentItem(step, true)
            updateProgress(step)
            updateButtonState(pagerAdapter.isStepValid(step))
        }
    }

    private fun updateSkipButtonVisibility(step: Int) {
        viewBind.btnSkip.visible()
//        viewBind.btnSkip.visibility = if (step >= 10) android.view.View.VISIBLE else android.view.View.GONE
    }

    // Fragment回调，输入有效性变化时调用
    override fun onInputValid(step: Int, valid: Boolean) {
        Log.e("UserInfoActivity", "onInputValid: step=$step, valid=$valid currentStep=$currentStep")
        if (step == currentStep) {
            updateButtonState(valid)
        }
        pagerAdapter.setStepValid(step, valid)
    }
}
