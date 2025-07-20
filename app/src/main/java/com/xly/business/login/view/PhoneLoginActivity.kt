package com.xly.business.login.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityPhoneLoginBinding
import android.content.Intent
import com.xly.R

class PhoneLoginActivity : LYBaseActivity<ActivityPhoneLoginBinding, LoginViewModel>() {
    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityPhoneLoginBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.btnBack.setOnClickListener { finish() }
        viewBind.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasInput = !s.isNullOrEmpty()
                viewBind.btnClear.visibility = if (hasInput) View.VISIBLE else View.GONE
                val enable = s?.length == 11
                viewBind.btnGetCode.isEnabled = enable
                viewBind.btnGetCode.alpha = if (enable) 1f else 0.5f
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        viewBind.btnClear.setOnClickListener { viewBind.etPhone.text?.clear() }
        viewBind.btnGetCode.setOnClickListener {
            val phone = viewBind.etPhone.text.toString()
            if (phone.length == 11) {
                // 跳转验证码登录页
                val intent = Intent(this, CodeLoginActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
            }
        }
    }
} 