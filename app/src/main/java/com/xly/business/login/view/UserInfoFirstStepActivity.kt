package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
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
    private var ageValue: String? = null
    private var heightValue: String? = null
    private var hometownValue: String? = null
    private var residenceValue: String? = null
    private var educationValue: String? = null
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
        setupAvatar()
        setupNickname()
        setupGender()
        setupAge()
        setupHeight()
        setupHometown()
        setupResidence()
        setupEducation()
        updateNextButtonState()
    }

    override fun initOnClick() {
        // 下一步按钮
        viewBind.btnNext.setOnClickListener {
            submitInfo()
        }
    }

    private fun setupAvatar() {
        viewBind.llAvatar.setOnClickListener {
            // TODO: 实现头像上传功能，可以弹出图片选择器
            // 例如：使用图片选择库（如 ImagePicker）或系统 Intent
            showToast("头像上传功能待实现")
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
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etNickname.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etNickname.clearFocus()
            // 隐藏键盘
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(viewBind.etNickname.windowToken, 0)
        }
    }

    private fun setupGender() {
        viewBind.rgGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.rbMale -> "1" // 男士
                R.id.rbFemale -> "2" // 女士
                else -> null
            }
            updateNextButtonState()
        }
    }

    private fun setupAge() {
        viewBind.llAge.setOnClickListener {
            showAgePicker()
        }
    }

    private fun setupHeight() {
        viewBind.llHeight.setOnClickListener {
            showHeightPicker()
        }
    }

    private fun setupHometown() {
        viewBind.llHometown.setOnClickListener {
            showHometownPicker()
        }
    }

    private fun setupResidence() {
        viewBind.llResidence.setOnClickListener {
            showResidencePicker()
        }
    }

    private fun setupEducation() {
        viewBind.llEducation.setOnClickListener {
            showEducationPicker()
        }
    }

    private fun showAgePicker() {
        val ageOptions = (18..50).map { "${it}岁" } + listOf("50+岁")
        BottomPickerDialog(
            this,
            "选择年龄",
            ageOptions
        ) { selected ->
            ageValue = selected
            viewBind.tvAge.text = selected
            viewBind.tvAge.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showHeightPicker() {
        val heightOptions = (140..210).map { "${it}cm" }
        BottomPickerDialog(
            this,
            "选择身高",
            heightOptions
        ) { selected ->
            heightValue = selected
            viewBind.tvHeight.text = selected
            viewBind.tvHeight.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showHometownPicker() {
        // 这里可以使用AddressPickerDialog或者简单的列表
        // 暂时使用简单的省份列表
        val provinceOptions = listOf(
            "北京", "上海", "天津", "重庆", "河北", "山西", "内蒙古", "辽宁",
            "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "广西", "海南", "四川", "贵州",
            "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "台湾", "香港", "澳门"
        )
        BottomPickerDialog(
            this,
            "选择家乡",
            provinceOptions
        ) { selected ->
            hometownValue = selected
            viewBind.tvHometown.text = selected
            viewBind.tvHometown.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showResidencePicker() {
        val provinceOptions = listOf(
            "北京", "上海", "天津", "重庆", "河北", "山西", "内蒙古", "辽宁",
            "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "广西", "海南", "四川", "贵州",
            "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆", "台湾", "香港", "澳门"
        )
        BottomPickerDialog(
            this,
            "选择现居住地",
            provinceOptions
        ) { selected ->
            residenceValue = selected
            viewBind.tvResidence.text = selected
            viewBind.tvResidence.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
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

    private fun updateNextButtonState() {
        val isValid = nickname.isNotEmpty() && 
                     selectedGender != null && 
                     ageValue != null && 
                     heightValue != null &&
                     hometownValue != null &&
                     residenceValue != null &&
                     educationValue != null
        
        viewBind.btnNext.isEnabled = isValid
    }

    private fun submitInfo() {
        if (!validateInput()) {
            return
        }

        // 保存到 ViewModel
        viewModel.nickname = nickname
        viewModel.gender = selectedGender
        // 解析年龄和身高数值
        val age = ageValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
        val height = heightValue?.replace("cm", "")?.toIntOrNull() ?: 0

        // 提交到服务器
        val request = UserInfoRegisterReq().apply {
            step = 1
            this.nickname = this@UserInfoFirstStepActivity.nickname
            this.gender = selectedGender ?: ""
            this.age = age
            this.height = height
            // 如果需要保存其他字段，可以在这里添加
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
        if (ageValue == null) {
            Toast.makeText(this, "请选择年龄", Toast.LENGTH_SHORT).show()
            return false
        }
        if (heightValue == null) {
            Toast.makeText(this, "请选择身高", Toast.LENGTH_SHORT).show()
            return false
        }
        if (hometownValue == null) {
            Toast.makeText(this, "请选择家乡", Toast.LENGTH_SHORT).show()
            return false
        }
        if (residenceValue == null) {
            Toast.makeText(this, "请选择现居住地", Toast.LENGTH_SHORT).show()
            return false
        }
        if (educationValue == null) {
            Toast.makeText(this, "请选择学历", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            hideLoading()
            // 跳转到下一步或主页面
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
