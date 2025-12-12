package com.xly.business.login.view

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityCodeLoginBinding
import com.xly.middlelibrary.utils.MMKVManager

class CodeLoginActivity : LYBaseActivity<ActivityCodeLoginBinding, LoginViewModel>() {

    private var phone = ""
    private var code = ""
    private var countDownTimer: CountDownTimer? = null
    private var countdownSeconds = 60

    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityCodeLoginBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.btnBack.setOnClickListener { finish() }
        phone = intent.getStringExtra("phone") ?: ""
        viewBind.tvPhone.text = phone
        
        // 设置获取验证码点击事件
        setupGetCodeButton()
        
        // 设置按钮固定宽度
        setupGetCodeButtonWidth()
        
        // 开始倒计时
        startCountdown()
        
        // 动态计算并设置输入框容器的左右边距
        setupCodeInputMargins()
        
        // 自动聚焦第一个输入框并弹出键盘
        autoFocusAndShowKeyboard()
        val codeInputs = listOf(viewBind.etCode1, viewBind.etCode2, viewBind.etCode3, viewBind.etCode4)
        for (i in codeInputs.indices) {
            codeInputs[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty() && i < codeInputs.size - 1) {
                        codeInputs[i + 1].requestFocus()
                    }
                    if (allCodeFilled(codeInputs)) {
                        code = codeInputs.joinToString(separator = "") { it.text.toString() }

                        // 模拟登录成功：保存登录和认证成功标识
                        MMKVManager.putBoolean(MMKVManager.KEY_LOGIN_SUCCESS, true)
                        MMKVManager.putBoolean(MMKVManager.KEY_AUTH_SUCCESS, true)

                        // 清除所有之前的Activity并跳转到主页面（用于本地调试）
                        /*ActivityStackManager.startActivityAndClearStack(
                            this@CodeLoginActivity,
                            LYMainActivity::class.java
                        )*/

                        // 清除所有之前的Activity并跳转到用户信息录入第一个页面
                        ActivityStackManager.startActivityAndClearStack(
                            this@CodeLoginActivity,
                            UserInfoActivity::class.java
                        ) {
                            putExtra("phone", phone)
                            putExtra("code", code)
                        }
                        
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
    
    /**
     * 动态计算并设置验证码输入框容器的左右边距，确保左右边距相等
     */
    private fun setupCodeInputMargins() {
        viewBind.llCodeContainer.post {
            val screenWidth = resources.displayMetrics.widthPixels
            
            // 外层容器的左右 padding（16dp）
            val outerPadding = 16.dpToPx()
            
            // 输入框宽度和间距
            val inputWidth = 56.dpToPx()
            val inputSpacing = 16.dpToPx()
            
            // 计算4个输入框和3个间距的总宽度
            val totalInputWidth = inputWidth * 4 + inputSpacing * 3
            
            // 可用宽度 = 屏幕宽度 - 外层左右padding
            val availableWidth = screenWidth - outerPadding * 2
            
            // 计算左右边距，使其相等
            // 左边距 = 右边距 = (可用宽度 - 输入框总宽度) / 2
            val sideMargin = (availableWidth - totalInputWidth) / 2
            
            // 确保边距不为负数
            val finalMargin = sideMargin.coerceAtLeast(0)
            
            // 设置左右边距
            val layoutParams = viewBind.llCodeContainer.layoutParams as android.view.ViewGroup.MarginLayoutParams
            layoutParams.marginStart = finalMargin
            layoutParams.marginEnd = finalMargin
            viewBind.llCodeContainer.layoutParams = layoutParams
        }
    }
    
    /**
     * dp转px的扩展函数
     */
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
    
    /**
     * 设置获取验证码按钮
     */
    private fun setupGetCodeButton() {
        viewBind.tvGetCode.setOnClickListener {
            if (countdownSeconds <= 0) {
                // 重新发送验证码
                // TODO: 调用发送验证码接口
                startCountdown()
            }
        }
    }
    
    /**
     * 自动聚焦第一个输入框并显示键盘
     */
    private fun autoFocusAndShowKeyboard() {
        viewBind.etCode1.postDelayed({
            viewBind.etCode1.requestFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(viewBind.etCode1, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }
    
    /**
     * 设置按钮固定宽度，确保倒计时和"获取验证码"文字时宽度一致
     */
    private fun setupGetCodeButtonWidth() {
        viewBind.tvGetCode.post {
            val paint = viewBind.tvGetCode.paint
            val textSize = viewBind.tvGetCode.textSize
            
            // 测量"获取验证码"文字的宽度
            val getCodeText = "获取验证码"
            val getCodeWidth = paint.measureText(getCodeText)
            
            // 测量"60s"文字的宽度（倒计时最大是60s）
            val countdownText = "60s"
            val countdownWidth = paint.measureText(countdownText)
            
            // 取两者中较大的宽度，并加上padding
            val buttonPadding = 16.dpToPx() * 2 // 左右padding各16dp
            val maxWidth = maxOf(getCodeWidth, countdownWidth).toInt() + buttonPadding
            
            // 设置按钮的最小宽度
            viewBind.tvGetCode.minWidth = maxWidth
        }
    }
    
    /**
     * 开始倒计时
     */
    private fun startCountdown() {
        countdownSeconds = 60
        countDownTimer?.cancel()
        
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownSeconds = (millisUntilFinished / 1000).toInt()
                // 倒计时显示在按钮上，文字居中
                viewBind.tvGetCode.text = "${countdownSeconds}s"
                viewBind.tvGetCode.gravity = android.view.Gravity.CENTER
                // 倒计时时禁用按钮，使用按压态背景
                viewBind.tvGetCode.isEnabled = false
                viewBind.tvGetCode.isClickable = false
                viewBind.tvGetCode.background = ContextCompat.getDrawable(this@CodeLoginActivity, com.xly.R.drawable.bg_get_code_button_countdown)
                viewBind.tvGetCode.setTextColor(ContextCompat.getColor(this@CodeLoginActivity, com.xly.R.color.text_white))
            }

            override fun onFinish() {
                countdownSeconds = 0
                // 倒计时结束，显示"获取验证码"
                viewBind.tvGetCode.text = "获取验证码"
                viewBind.tvGetCode.gravity = android.view.Gravity.CENTER
                // 倒计时结束，启用按钮
                viewBind.tvGetCode.isEnabled = true
                viewBind.tvGetCode.isClickable = true
                viewBind.tvGetCode.background = ContextCompat.getDrawable(this@CodeLoginActivity, com.xly.R.drawable.bg_get_code_button)
                viewBind.tvGetCode.setTextColor(ContextCompat.getColor(this@CodeLoginActivity, com.xly.R.color.text_white))
            }
        }.start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }


    override fun initObservers() {

        // 观察登录结果
        viewModel.loginResult.observe(this) { authResponse ->
            // 处理登录成功
            Log.i("CodeLoginActivity","success")
            hideLoading()
            MMKVManager.putBoolean(MMKVManager.KEY_LOGIN_SUCCESS, true)
            
            // 清除所有之前的Activity并跳转到用户信息录入第一个页面
            ActivityStackManager.startActivityAndClearStack(
                this@CodeLoginActivity,
                UserInfoActivity::class.java
            ) {
                putExtra("phone", phone)
                putExtra("code", code)
            }
        }
        viewModel.errorMessage.observe(this) { errorMessage ->
            hideLoading()
            showToast(errorMessage)
        }

    }


    private fun requestLogin(phoneNum: String, code: String) {
        showLoading()
        viewModel.phoneLogin(phoneNum,code)
    }


    private fun allCodeFilled(inputs: List<android.widget.EditText>): Boolean {
        return inputs.all { it.text?.length == 1 }
    }
} 