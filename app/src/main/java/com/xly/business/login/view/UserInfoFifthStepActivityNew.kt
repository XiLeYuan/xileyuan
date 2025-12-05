package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoFifthStepRequest
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoFifthStepBinding
import com.xly.index.LYMainActivity

class UserInfoFifthStepActivityNew : LYBaseActivity<ActivityUserInfoFifthStepBinding, LoginViewModel>() {

    private var idealPartner: String = ""
    private var ageMinValue: String? = null
    private var ageMaxValue: String? = null
    private var heightMinValue: String? = null
    private var heightMaxValue: String? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoFifthStepActivityNew::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoFifthStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupIdealPartner()
        setupAgeRange()
        setupHeightRange()
        updateNextButtonState()
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etIdealPartner.clearFocus()
            // 隐藏键盘
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            val currentFocus = currentFocus
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }
        }
    }

    override fun initOnClick() {
        // 下一步按钮
        viewBind.btnNext.setOnClickListener {
            if (validateInput()) {
                submitInfo()
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

    private fun setupIdealPartner() {
        viewBind.etIdealPartner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                idealPartner = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etIdealPartner.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
    }

    private fun setupAgeRange() {
        viewBind.llAgeMin.setOnClickListener {
            showAgeMinPicker()
        }
        
        viewBind.llAgeMax.setOnClickListener {
            showAgeMaxPicker()
        }
    }

    private fun setupHeightRange() {
        viewBind.llHeightMin.setOnClickListener {
            showHeightMinPicker()
        }
        
        viewBind.llHeightMax.setOnClickListener {
            showHeightMaxPicker()
        }
    }

    private fun showAgeMinPicker() {
        val ageOptions = (18..50).map { "${it}岁" } + listOf("50+岁")
        BottomPickerDialog(
            this,
            "选择最小年龄",
            ageOptions
        ) { selected ->
            ageMinValue = selected
            viewBind.tvAgeMin.text = selected
            viewBind.tvAgeMin.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showAgeMaxPicker() {
        val ageOptions = (18..50).map { "${it}岁" } + listOf("50+岁")
        BottomPickerDialog(
            this,
            "选择最大年龄",
            ageOptions
        ) { selected ->
            ageMaxValue = selected
            viewBind.tvAgeMax.text = selected
            viewBind.tvAgeMax.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showHeightMinPicker() {
        val heightOptions = (140..210).map { "${it}cm" }
        HeightPickerDialog(
            this,
            heightOptions,
            heightMinValue
        ) { selected ->
            heightMinValue = selected
            viewBind.tvHeightMin.text = selected
            viewBind.tvHeightMin.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showHeightMaxPicker() {
        val heightOptions = (140..210).map { "${it}cm" }
        HeightPickerDialog(
            this,
            heightOptions,
            heightMaxValue
        ) { selected ->
            heightMaxValue = selected
            viewBind.tvHeightMax.text = selected
            viewBind.tvHeightMax.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun updateNextButtonState() {
        // 择偶描述必填，年龄和身高范围为可选
        val isValid = idealPartner.isNotEmpty()
        
        viewBind.btnNext.isEnabled = isValid
    }

    private fun submitInfo() {
        if (!validateInput()) {
            return
        }

        // 显示按钮上的进度条，隐藏箭头，禁用按钮
        viewBind.progressBar.visibility = android.view.View.VISIBLE
        viewBind.ivArrow.visibility = android.view.View.GONE
        viewBind.btnNext.isEnabled = false
        viewBind.btnNext.isClickable = false

        // 解析年龄和身高数值
        val ageMin = ageMinValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
        val ageMax = ageMaxValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
        val heightMin = heightMinValue?.replace("cm", "")?.toIntOrNull() ?: 0
        val heightMax = heightMaxValue?.replace("cm", "")?.toIntOrNull() ?: 0

        // 创建第五步请求实体
        val fifthStepRequest = UserInfoFifthStepRequest().apply {
            this.idealPartner = this@UserInfoFifthStepActivityNew.idealPartner
            this.preferredAgeMin = ageMin
            this.preferredAgeMax = ageMax
            this.preferredHeightMin = heightMin
            this.preferredHeightMax = heightMax
        }

        // 同时更新到 UserInfoRegisterReq（用于兼容现有接口）
        val request = UserInfoRegisterReq().apply {
            step = 5
            this.idealPartner = fifthStepRequest.idealPartner
            this.preferredAgeMin = fifthStepRequest.preferredAgeMin.toString()
            this.preferredAgeMax = fifthStepRequest.preferredAgeMax.toString()
            // 注意：UserInfoRegisterReq中可能没有身高范围字段，需要检查
        }

        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (idealPartner.isEmpty()) {
            Toast.makeText(this, "请输入择偶描述", Toast.LENGTH_SHORT).show()
            return false
        }
        
        // 验证年龄范围
        if (ageMinValue != null && ageMaxValue != null) {
            val ageMin = ageMinValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
            val ageMax = ageMaxValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
            if (ageMin > 0 && ageMax > 0 && ageMin > ageMax) {
                Toast.makeText(this, "最小年龄不能大于最大年龄", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        
        // 验证身高范围
        if (heightMinValue != null && heightMaxValue != null) {
            val heightMin = heightMinValue?.replace("cm", "")?.toIntOrNull() ?: 0
            val heightMax = heightMaxValue?.replace("cm", "")?.toIntOrNull() ?: 0
            if (heightMin > 0 && heightMax > 0 && heightMin > heightMax) {
                Toast.makeText(this, "最小身高不能大于最大身高", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        
        return true
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            // 恢复按钮状态
            viewBind.progressBar.visibility = android.view.View.GONE
            viewBind.ivArrow.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = true
            viewBind.btnNext.isClickable = true
            
            // 跳转到第六步（身份验证）
            UserInfoSixthStepActivity.start(this)
            finish()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            // 恢复按钮状态
            viewBind.progressBar.visibility = android.view.View.GONE
            viewBind.ivArrow.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = true
            viewBind.btnNext.isClickable = true
            
            showToast(errorMessage)
        }
    }
}

