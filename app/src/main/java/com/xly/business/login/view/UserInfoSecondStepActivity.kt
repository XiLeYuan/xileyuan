package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoSecondStepRequest
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoSecondStepBinding
import com.xly.index.LYMainActivity

class UserInfoSecondStepActivity : LYBaseActivity<ActivityUserInfoSecondStepBinding, LoginViewModel>() {

    private var educationValue: String? = null
    private var schoolValue: String = ""
    private var jobValue: String = ""
    private var incomeValue: String? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoSecondStepActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoSecondStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupEducation()
        setupSchool()
        setupJob()
        setupIncome()
        updateNextButtonState()
    }

    override fun initOnClick() {
        // 下一步按钮
        viewBind.btnNext.setOnClickListener {
            if (validateInput()) {
                simulateNetworkRequest()
            }
        }
        
        // 跳过按钮 - 直接进入首页
        viewBind.tvSkip.setOnClickListener {
            ActivityStackManager.startActivityAndClearStack(
                this,
                LYMainActivity::class.java
            )
        }
    }

    private fun setupEducation() {
        viewBind.llEducation.setOnClickListener {
            showEducationPicker()
        }
    }

    private fun setupSchool() {
        viewBind.etSchool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                schoolValue = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etSchool.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etSchool.clearFocus()
            // 隐藏键盘
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(viewBind.etSchool.windowToken, 0)
        }
    }

    private fun setupJob() {
        viewBind.etJob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                jobValue = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etJob.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etJob.clearFocus()
            // 隐藏键盘
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(viewBind.etJob.windowToken, 0)
        }
    }

    private fun setupIncome() {
        viewBind.llIncome.setOnClickListener {
            showIncomePicker()
        }
    }

    private fun showEducationPicker() {
        val educationOptions = listOf(
            "初中及以下", "高中/中专", "大专", "本科", "硕士", "博士"
        )
        BottomPickerDialog(
            this,
            "选择学历",
            educationOptions
        ) { selected ->
            educationValue = selected
            viewBind.tvEducation.text = selected
            viewBind.tvEducation.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showIncomePicker() {
        val incomeOptions = listOf(
            "3000元以下", "3000-5000元", "5000-8000元", "8000-12000元", 
            "12000-20000元", "20000-30000元", "30000-50000元", "50000元以上"
        )
        BottomPickerDialog(
            this,
            "选择收入",
            incomeOptions
        ) { selected ->
            incomeValue = selected
            viewBind.tvIncome.text = selected
            viewBind.tvIncome.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun updateNextButtonState() {
        // 学历、职业、收入必填，学校选填
        val isValid = educationValue != null && 
                     jobValue.isNotEmpty() &&
                     incomeValue != null
        
        viewBind.btnNext.isEnabled = isValid
    }

    private fun submitInfo() {
        if (!validateInput()) {
            return
        }

        // 保存到 ViewModel
        viewModel.education = educationValue
        viewModel.school = schoolValue
        viewModel.job = jobValue
        viewModel.income = incomeValue

        // 创建第二步请求实体
        val secondStepRequest = UserInfoSecondStepRequest().apply {
            this.education = this@UserInfoSecondStepActivity.educationValue ?: ""
            this.school = this@UserInfoSecondStepActivity.schoolValue
            this.occupation = this@UserInfoSecondStepActivity.jobValue
            this.incomeLevel = this@UserInfoSecondStepActivity.incomeValue ?: ""
        }

        // 同时更新到 UserInfoRegisterReq（用于兼容现有接口）
        val request = UserInfoRegisterReq().apply {
            step = 2
            // 学历需要转换为对应的数字等级（1-6）
            this.educationLevel = when (secondStepRequest.education) {
                "初中及以下" -> true // 1
                "高中/中专" -> true // 2
                "大专" -> true // 3
                "本科" -> true // 4
                "硕士" -> true // 5
                "博士" -> true // 6
                else -> true
            }
            this.school = secondStepRequest.school
            this.occupation = secondStepRequest.occupation
            this.incomeLevel = secondStepRequest.incomeLevel
        }

        showLoading()
        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (educationValue == null) {
            Toast.makeText(this, "请选择学历", Toast.LENGTH_SHORT).show()
            return false
        }
        if (jobValue.isEmpty()) {
            Toast.makeText(this, "请输入职业", Toast.LENGTH_SHORT).show()
            return false
        }
        if (incomeValue == null) {
            Toast.makeText(this, "请选择收入", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun simulateNetworkRequest() {
        // 显示按钮上的进度条，隐藏箭头，禁用按钮
        viewBind.progressBar.visibility = android.view.View.VISIBLE
        viewBind.ivArrow.visibility = android.view.View.GONE
        viewBind.btnNext.isEnabled = false
        viewBind.btnNext.isClickable = false
        
        // 保存到 ViewModel
        viewModel.education = educationValue
        viewModel.school = schoolValue
        viewModel.job = jobValue
        viewModel.income = incomeValue
        
        // 模拟网络请求延迟（1.5秒）
        Handler(Looper.getMainLooper()).postDelayed({
            // 恢复按钮状态
            viewBind.progressBar.visibility = android.view.View.GONE
            viewBind.ivArrow.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = true
            viewBind.btnNext.isClickable = true
            
            // 跳转到第三步
            UserInfoThirdStepActivity.start(this)
            finish()
        }, 1500)
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            hideLoading()
            // 跳转到第三步
            UserInfoThirdStepActivity.start(this)
            finish()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            hideLoading()
            showToast(errorMessage)
        }
    }
}

