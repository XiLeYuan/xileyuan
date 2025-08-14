package com.xly.business.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.LoginUser
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.business.recommend.model.User
import com.xly.business.user.UserInfo
import com.xly.databinding.ActivityCodeLoginBinding
import com.xly.index.LYMainActivity

class CodeLoginActivity : LYBaseActivity<ActivityCodeLoginBinding, LoginViewModel>() {
    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityCodeLoginBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.btnBack.setOnClickListener { finish() }
        val phone = intent.getStringExtra("phone") ?: ""
        viewBind.tvPhone.text = phone
        // 自动聚焦第一个输入框
        viewBind.etCode1.requestFocus()
        val codeInputs = listOf(viewBind.etCode1, viewBind.etCode2, viewBind.etCode3, viewBind.etCode4)
        for (i in codeInputs.indices) {
            codeInputs[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty() && i < codeInputs.size - 1) {
                        codeInputs[i + 1].requestFocus()
                    }
                    if (allCodeFilled(codeInputs)) {
                        val code = codeInputs.joinToString(separator = "") { it.text.toString() }
                        requestLogin(phone,code)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            codeInputs[i].setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (codeInputs[i].text.isNullOrEmpty() && i > 0) {
                        codeInputs[i - 1].requestFocus()
                        codeInputs[i - 1].setSelection(codeInputs[i - 1].text?.length ?: 0)
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }


    override fun initObservers() {

        // 观察登录结果
        viewModel.loginResult.observe(this) { authResponse ->
            // 处理登录成功
            Log.i("CodeLoginActivity","success")
        }

    }


    private fun requestLogin(phoneNum: String, code: String) {

        viewModel.phoneLogin(phoneNum,code)
//        viewModel.getHealth()

        // 跳转到个人信息收集页面
        /*val intent = Intent(this@CodeLoginActivity, UserInfoActivity::class.java)
        intent.putExtra("phone", phone)
        intent.putExtra("code", code)
        startActivity(intent)
        finish()*/

    }


    private fun allCodeFilled(inputs: List<android.widget.EditText>): Boolean {
        return inputs.all { it.text?.length == 1 }
    }
} 