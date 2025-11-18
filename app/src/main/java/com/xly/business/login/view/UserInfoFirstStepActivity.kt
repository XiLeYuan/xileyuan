package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoFirstStepBinding
import com.xly.index.LYMainActivity

class UserInfoFirstStepActivity : LYBaseActivity<ActivityUserInfoFirstStepBinding, LoginViewModel>() {

    private var selectedGender: String? = null
    private var ageValue = 0
    private var heightValue = 0
    private var nickname: String = ""

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoFirstStepActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoFirstStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupNickname()
        setupGender()
        setupAge()
        setupHeight()
        updateNextButtonState()
    }

    override fun initOnClick() {
        // 下一步按钮
        viewBind.btnNext.setOnClickListener {
            submitInfo()
        }
    }

    private fun setupNickname() {
        viewBind.etNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nickname = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupGender() {
        val genderOptions = listOf("男士", "女士")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
        )
        
        viewBind.actvGender.setAdapter(adapter)
        
        // 防止键盘弹出
        viewBind.actvGender.keyListener = null
        
        // 点击时显示下拉菜单
        viewBind.actvGender.setOnClickListener {
            viewBind.actvGender.showDropDown()
        }
        
        // 选择项时更新状态
        viewBind.actvGender.setOnItemClickListener { _, _, position, _ ->
            selectedGender = if (position == 0) "1" else "2" // 0=男士, 1=女士
            updateNextButtonState()
        }
    }

    private fun setupAge() {
        viewBind.seekBarAge.max = 32 // 18~50+ 共33档
        viewBind.seekBarAge.progress = 0
        viewBind.tvAgeBubble.visibility = View.GONE

        viewBind.seekBarAge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val age = progress + 18
                ageValue = if (progress == 32) 51 else age
                val ageText = if (progress == 32) "50+岁" else "${age}岁"
                viewBind.tvAgeBubble.text = ageText
                viewBind.tvAgeBubble.visibility = View.VISIBLE
                
                // 气泡位置
                val bubbleWidth = viewBind.tvAgeBubble.width.toFloat()
                val seekBarWidth = viewBind.seekBarAge.width.toFloat()
                if (seekBarWidth > 0 && bubbleWidth > 0) {
                    val percent = progress.toFloat() / viewBind.seekBarAge.max
                    val translationX = (seekBarWidth - bubbleWidth) * percent
                    viewBind.tvAgeBubble.translationX = translationX
                }
                
                updateNextButtonState()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewBind.tvAgeBubble.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 初始化时计算一次气泡位置
        viewBind.tvAgeBubble.post {
            val progress = viewBind.seekBarAge.progress
            val bubbleWidth = viewBind.tvAgeBubble.width.toFloat()
            val seekBarWidth = viewBind.seekBarAge.width.toFloat()
            if (seekBarWidth > 0 && bubbleWidth > 0) {
                val percent = progress.toFloat() / viewBind.seekBarAge.max
                val translationX = (seekBarWidth - bubbleWidth) * percent
                viewBind.tvAgeBubble.translationX = translationX
            }
        }
    }

    private fun setupHeight() {
        viewBind.seekBarHeight.max = 70 // 140~210 共71档
        viewBind.seekBarHeight.progress = 0
        viewBind.tvHeightBubble.visibility = View.GONE

        viewBind.seekBarHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val height = progress + 140
                heightValue = height
                val heightText = "${height}cm"
                viewBind.tvHeightBubble.text = heightText
                viewBind.tvHeightBubble.visibility = View.VISIBLE
                
                // 气泡位置
                val bubbleWidth = viewBind.tvHeightBubble.width.toFloat()
                val seekBarWidth = viewBind.seekBarHeight.width.toFloat()
                if (seekBarWidth > 0 && bubbleWidth > 0) {
                    val percent = progress.toFloat() / viewBind.seekBarHeight.max
                    val translationX = (seekBarWidth - bubbleWidth) * percent
                    viewBind.tvHeightBubble.translationX = translationX
                }
                
                updateNextButtonState()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                viewBind.tvHeightBubble.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 初始化时计算一次气泡位置
        viewBind.tvHeightBubble.post {
            val progress = viewBind.seekBarHeight.progress
            val bubbleWidth = viewBind.tvHeightBubble.width.toFloat()
            val seekBarWidth = viewBind.seekBarHeight.width.toFloat()
            if (seekBarWidth > 0 && bubbleWidth > 0) {
                val percent = progress.toFloat() / viewBind.seekBarHeight.max
                val translationX = (seekBarWidth - bubbleWidth) * percent
                viewBind.tvHeightBubble.translationX = translationX
            }
        }
    }

    private fun updateNextButtonState() {
        val isValid = nickname.isNotEmpty() && 
                     selectedGender != null && 
                     ageValue > 0 && 
                     heightValue > 0
        
        viewBind.btnNext.isEnabled = isValid
        viewBind.btnNext.alpha = if (isValid) 1f else 0.5f
    }

    private fun submitInfo() {
        if (!validateInput()) {
            return
        }

        // 保存到 ViewModel
        viewModel.nickname = nickname
        viewModel.gender = selectedGender
        viewModel.age = ageValue
        viewModel.height = heightValue

        // 提交到服务器
        val request = UserInfoRegisterReq().apply {
            step = 1
            this.nickname = this@UserInfoFirstStepActivity.nickname
            this.gender = selectedGender ?: ""
            this.age = ageValue
            this.height = heightValue
        }

        showLoading()
        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedGender == null) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ageValue == 0) {
            Toast.makeText(this, "请选择年龄", Toast.LENGTH_SHORT).show()
            return false
        }
        if (heightValue == 0) {
            Toast.makeText(this, "请选择身高", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            hideLoading()
            // 跳转到下一步或主页面
            // 这里可以根据业务需求跳转到 UserInfoActivity 的下一步，或者直接跳转到主页
            ActivityStackManager.startActivityAndClearStack(
                this,
                LYMainActivity::class.java
            )
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            hideLoading()
            showToast(errorMessage)
        }
    }
}

