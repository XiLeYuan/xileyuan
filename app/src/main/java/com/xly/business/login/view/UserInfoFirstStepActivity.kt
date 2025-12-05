package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoFirstStepBinding
import com.xly.index.LYMainActivity
import com.xly.middlelibrary.utils.GlideEngine
import com.xly.middlelibrary.utils.LYUtils
import java.io.File

class UserInfoFirstStepActivity : LYBaseActivity<ActivityUserInfoFirstStepBinding, LoginViewModel>() {

    private var selectedGender: String? = null
    private var ageValue: String? = null
    private var heightValue: String? = null
    private var hometownValue: String? = null
    private var hometownProvince: String? = null
    private var hometownCity: String? = null
    private var hometownDistrict: String? = null
    private var residenceValue: String? = null
    private var residenceProvince: String? = null
    private var residenceCity: String? = null
    private var residenceDistrict: String? = null
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

    private fun setupAvatar() {
        viewBind.llAvatar.setOnClickListener {
            showAvatarPickerDialog()
        }
    }

    private fun showAvatarPickerDialog() {
        val dialog = AvatarPickerBottomSheetDialogFragment().apply {
            onCameraClick = {
                selectAvatarFromCamera()
            }
            onGalleryClick = {
                selectAvatarFromGallery()
            }
        }
        dialog.show(supportFragmentManager, "AvatarPickerDialog")
    }

    private fun selectAvatarFromCamera() {
        // 检查权限
        if (!LYUtils.checkStoragePermission(this)) {
            LYUtils.requestStoragePermission(this)
            return
        }
        if (!LYUtils.checkCameraPermission(this)) {
            LYUtils.requestCameraPermission(this)
            return
        }

        // 使用PictureSelector拍照，启用裁剪，设置高大于宽的比例（3:2，高度:宽度）
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .setCropEngine(com.xly.middlelibrary.utils.AvatarCropEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        val localMedia = result[0]
                        val filePath = localMedia?.availablePath
                        if (!filePath.isNullOrEmpty()) {
                            // 如果是 content:// URI，直接使用 Uri，否则使用 File
                            if (filePath.startsWith("content://")) {
                                Glide.with(this@UserInfoFirstStepActivity)
                                    .load(android.net.Uri.parse(filePath))
                                    .circleCrop()
                                    .into(viewBind.ivAvatar)
                            } else {
                                processSelectedImage(File(filePath))
                            }
                        }
                    }
                }

                override fun onCancel() {
                    // 用户取消拍照
                }
            })
    }

    private fun selectAvatarFromGallery() {
        // 检查权限
        if (!LYUtils.checkStoragePermission(this)) {
            LYUtils.requestStoragePermission(this)
            return
        }

        // 使用PictureSelector选择图片，启用裁剪，设置高大于宽的比例（3:2，高度:宽度）
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.instance)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setCropEngine(com.xly.middlelibrary.utils.AvatarCropEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        val localMedia = result[0]
                        val filePath = localMedia?.availablePath
                        if (!filePath.isNullOrEmpty()) {
                            // 如果是 content:// URI，直接使用 Uri，否则使用 File
                            if (filePath.startsWith("content://")) {
                                Glide.with(this@UserInfoFirstStepActivity)
                                    .load(android.net.Uri.parse(filePath))
                                    .circleCrop()
                                    .into(viewBind.ivAvatar)
                            } else {
                                processSelectedImage(File(filePath))
                            }
                        }
                    }
                }

                override fun onCancel() {
                    // 用户取消选择
                }
            })
    }

    private fun processSelectedImage(imageFile: File) {
        // 显示选择的图片
        // 使用文件路径字符串加载，兼容 content:// URI
        val imagePath = imageFile.absolutePath
        Glide.with(this)
            .load(if (imagePath.startsWith("content://")) android.net.Uri.parse(imagePath) else imageFile)
            .circleCrop()
            .into(viewBind.ivAvatar)
        
        // 保存图片路径到ViewModel（如果需要上传到服务器）
        // viewModel.avatarPath = imagePath
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
        HeightPickerDialog(
            this,
            heightOptions,
            heightValue
        ) { selected ->
            heightValue = selected
            viewBind.tvHeight.text = selected
            viewBind.tvHeight.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showHometownPicker() {
        val dialog = AddressPickerDialog(
            this,
            "选择家乡"
        ) { province, city, district ->
            hometownProvince = province
            hometownCity = city
            hometownDistrict = district
            // 显示格式：省 市 区，例如：北京 北京市 朝阳区
            hometownValue = "$province $city $district"
            viewBind.tvHometown.text = hometownValue
            viewBind.tvHometown.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }
        // 如果有已选中的值，回显
        dialog.setSelectedAddress(hometownProvince, hometownCity, hometownDistrict)
        dialog.show()
    }

    private fun showResidencePicker() {
        val dialog = AddressPickerDialog(
            this,
            "选择现居住地"
        ) { province, city, district ->
            residenceProvince = province
            residenceCity = city
            residenceDistrict = district
            // 显示格式：省 市 区，例如：北京 北京市 朝阳区
            residenceValue = "$province $city $district"
            viewBind.tvResidence.text = residenceValue
            viewBind.tvResidence.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }
        // 如果有已选中的值，回显
        dialog.setSelectedAddress(residenceProvince, residenceCity, residenceDistrict)
        dialog.show()
    }

    private fun updateNextButtonState() {
        val isValid = nickname.isNotEmpty() && 
                     selectedGender != null && 
                     ageValue != null && 
                     heightValue != null &&
                     hometownValue != null &&
                     residenceValue != null
        
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
        return true
    }

    private fun simulateNetworkRequest() {
        // 显示按钮上的进度条，隐藏箭头，禁用按钮
        viewBind.progressBar.visibility = android.view.View.VISIBLE
        viewBind.ivArrow.visibility = android.view.View.GONE
        viewBind.btnNext.isEnabled = false
        viewBind.btnNext.isClickable = false
        
        // 保存到 ViewModel
        viewModel.nickname = nickname
        viewModel.gender = selectedGender
        // 解析年龄和身高数值
        val age = ageValue?.replace("岁", "")?.replace("+", "")?.toIntOrNull() ?: 0
        val height = heightValue?.replace("cm", "")?.toIntOrNull() ?: 0
        
        // 模拟网络请求延迟（1.5秒）
        Handler(Looper.getMainLooper()).postDelayed({
            // 恢复按钮状态
            viewBind.progressBar.visibility = android.view.View.GONE
            viewBind.ivArrow.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = true
            viewBind.btnNext.isClickable = true
            
            // 跳转到第三步
//            UserInfoThirdStepActivity.start(this)
            UserInfoSecondStepActivity.start(this)
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
