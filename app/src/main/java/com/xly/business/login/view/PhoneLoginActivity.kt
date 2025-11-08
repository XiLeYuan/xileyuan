package com.xly.business.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityPhoneLoginBinding

class PhoneLoginActivity : LYBaseActivity<ActivityPhoneLoginBinding, LoginViewModel>() {
    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityPhoneLoginBinding.inflate(layoutInflater)
    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    private var isKeyboardVisible = false
    private var lastKeyboardHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBind.btnBack.setOnClickListener { finish() }
        
        // 设置点击外部区域收起键盘
        setupClickOutsideToHideKeyboard()
        
        // 设置键盘监听
        setupKeyboardListener()
        
        // 自动获取焦点并显示键盘
        autoFocusAndShowKeyboard()
        
        // 监听输入框文本变化，控制按钮状态
        viewBind.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val enable = s?.length == 11 && isValidPhoneNumber(s.toString())
                viewBind.btnNext.isEnabled = enable
                viewBind.btnNext.alpha = if (enable) 1f else 0.5f
                
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
        
        viewBind.btnNext.setOnClickListener {
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
     * 设置键盘监听，让按钮跟随键盘动画
     */
    private fun setupKeyboardListener() {
        val rootView = viewBind.root
        val button = viewBind.btnNext
        
        // 使用 WindowInsets 监听键盘（Android 11+）
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            
            val keyboardHeight = imeInsets.bottom - navigationBars.bottom
            val wasVisible = isKeyboardVisible
            
            if (keyboardHeight > 0) {
                // 键盘显示
                if (!wasVisible) {
                    isKeyboardVisible = true
                    animateButtonUp(keyboardHeight)
                } else if (keyboardHeight != lastKeyboardHeight) {
                    // 键盘高度变化
                    animateButtonToPosition(keyboardHeight)
                }
                lastKeyboardHeight = keyboardHeight
            } else {
                // 键盘隐藏
                if (wasVisible) {
                    isKeyboardVisible = false
                    animateButtonDown()
                    lastKeyboardHeight = 0
                }
            }
            
            insets
        }
        
        // 兼容旧版本：使用 ViewTreeObserver 监听布局变化
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = android.graphics.Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height
                val keypadHeight = screenHeight - rect.bottom
                
                val wasVisible = isKeyboardVisible
                val isVisible = keypadHeight > screenHeight * 0.15 // 键盘高度超过屏幕15%认为键盘显示
                
                if (isVisible != wasVisible) {
                    isKeyboardVisible = isVisible
                    if (isVisible) {
                        animateButtonUp(keypadHeight)
                        lastKeyboardHeight = keypadHeight
                    } else {
                        animateButtonDown()
                        lastKeyboardHeight = 0
                    }
                } else if (isVisible && keypadHeight != lastKeyboardHeight) {
                    animateButtonToPosition(keypadHeight)
                    lastKeyboardHeight = keypadHeight
                }
            }
        })
    }
    
    /**
     * 按钮上移动画（键盘弹起）
     */
    private fun animateButtonUp(keyboardHeight: Int) {
        val button = viewBind.btnNext
        // 按钮需要上移键盘高度，保持原有的底部间距
        val targetY = -keyboardHeight.toFloat()
        
        button.animate()
            .translationY(targetY)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
    
    /**
     * 按钮下移动画（键盘收起）
     */
    private fun animateButtonDown() {
        val button = viewBind.btnNext
        button.animate()
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
    
    /**
     * 按钮移动到新位置（键盘高度变化）
     */
    private fun animateButtonToPosition(keyboardHeight: Int) {
        val button = viewBind.btnNext
        val targetY = -keyboardHeight.toFloat()
        
        button.animate()
            .translationY(targetY)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
    


    /**
     * 设置点击外部区域收起键盘
     */
    private fun setupClickOutsideToHideKeyboard() {
        val rootView = viewBind.root
        val inputLayout = viewBind.textInputLayoutPhone
        val editText = viewBind.etPhone
        val button = viewBind.btnNext
        
        // 使用触摸事件检测点击位置
        rootView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                
                // 获取输入框在屏幕上的位置
                val inputLocation = IntArray(2)
                inputLayout.getLocationOnScreen(inputLocation)
                val inputRect = android.graphics.Rect(
                    inputLocation[0],
                    inputLocation[1],
                    inputLocation[0] + inputLayout.width,
                    inputLocation[1] + inputLayout.height
                )
                
                // 获取按钮在屏幕上的位置
                val buttonLocation = IntArray(2)
                button.getLocationOnScreen(buttonLocation)
                val buttonRect = android.graphics.Rect(
                    buttonLocation[0],
                    buttonLocation[1],
                    buttonLocation[0] + button.width,
                    buttonLocation[1] + button.height
                )
                
                // 如果点击不在输入框和按钮上，收起键盘
                if (!inputRect.contains(x, y) && !buttonRect.contains(x, y)) {
                    hideKeyboard()
                }
            }
            false // 不拦截事件，让其他view正常处理
        }
    }
    
    /**
     * 隐藏键盘并让输入框失去焦点
     */
    private fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(viewBind.etPhone.windowToken, 0)
        viewBind.etPhone.clearFocus()
    }
    
    /**
     * 自动获取焦点并显示键盘
     */
    private fun autoFocusAndShowKeyboard() {
        // 延迟执行，确保布局完成
        viewBind.etPhone.postDelayed({
            // 请求焦点
            if (viewBind.etPhone.requestFocus()) {
                // 显示软键盘
                val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
                imm?.showSoftInput(viewBind.etPhone, InputMethodManager.SHOW_IMPLICIT)
            }
        }, 100) // 延迟100ms，确保布局完全渲染
    }
    
    override fun onResume() {
        super.onResume()
        // 在 onResume 中再次确保焦点和键盘显示
        viewBind.etPhone.postDelayed({
            if (!viewBind.etPhone.hasFocus()) {
                viewBind.etPhone.requestFocus()
                val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
                imm?.showSoftInput(viewBind.etPhone, InputMethodManager.SHOW_IMPLICIT)
            }
        }, 50)
    }
    
    /**
     * 验证手机号格式（简单验证：11位数字，以1开头）
     */
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }
} 