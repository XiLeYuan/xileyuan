package com.xly.business.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityPhoneLoginBinding

class PhoneLoginActivity : LYBaseActivity<ActivityPhoneLoginBinding, LoginViewModel>() {
    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityPhoneLoginBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.btnBack.setOnClickListener { finish() }
        
        // 监听输入框文本变化，控制按钮状态
        viewBind.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enable = s?.length == 11
                viewBind.btnGetCode.isEnabled = enable
                viewBind.btnGetCode.alpha = if (enable) 1f else 0.5f
                
                // 验证手机号格式
                if (!s.isNullOrEmpty() && s.length == 11) {
                    val phone = s.toString()
                    if (!isValidPhoneNumber(phone)) {
                        viewBind.textInputLayoutPhone.error = "请输入正确的手机号"
                    } else {
                        viewBind.textInputLayoutPhone.error = null
                    }
                } else {
                    viewBind.textInputLayoutPhone.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        viewBind.btnGetCode.setOnClickListener {
            val phone = viewBind.etPhone.text.toString()
            if (phone.length == 11 && isValidPhoneNumber(phone)) {
                // 跳转验证码登录页
                val intent = Intent(this, CodeLoginActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
            } else {
                viewBind.textInputLayoutPhone.error = "请输入正确的手机号"
            }
        }
    }
    
    /**
     * 验证手机号格式（简单验证：11位数字，以1开头）
     */
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }
} 