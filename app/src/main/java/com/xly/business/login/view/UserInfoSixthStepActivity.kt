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
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoSixthStepBinding
import com.xly.index.LYMainActivity

class UserInfoSixthStepActivity : LYBaseActivity<ActivityUserInfoSixthStepBinding, LoginViewModel>() {

    private var nameValue: String = ""
    private var idCardValue: String = ""

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoSixthStepActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoSixthStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupName()
        setupIdCard()
        updateNextButtonState()
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etName.clearFocus()
            viewBind.etIdCard.clearFocus()
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

    private fun setupName() {
        viewBind.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameValue = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etName.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
    }

    private fun setupIdCard() {
        viewBind.etIdCard.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                idCardValue = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etIdCard.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
    }

    private fun updateNextButtonState() {
        // 姓名和身份证号必填
        val isValid = nameValue.isNotEmpty() && 
                     idCardValue.isNotEmpty()
        
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

        // 保存到 ViewModel
        viewModel.realName = nameValue
        viewModel.idCardNumber = idCardValue

        // 提交到服务器
        val request = UserInfoRegisterReq().apply {
            step = 6
            this.realName = this@UserInfoSixthStepActivity.nameValue
            this.idCardNumber = this@UserInfoSixthStepActivity.idCardValue
        }

        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (nameValue.isEmpty()) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show()
            return false
        }
        if (idCardValue.isEmpty()) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show()
            return false
        }
        // 简单的身份证号格式验证（18位或15位）
        if (idCardValue.length != 15 && idCardValue.length != 18) {
            Toast.makeText(this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show()
            return false
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
            
            // 跳转到主页面
            ActivityStackManager.startActivityAndClearStack(
                this,
                LYMainActivity::class.java
            )
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

