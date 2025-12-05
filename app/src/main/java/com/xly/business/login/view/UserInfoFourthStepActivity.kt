package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoFourthStepRequest
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.view.adapter.LifePhotoAdapter4
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoFourthStepBinding
import com.xly.index.LYMainActivity
import com.xly.middlelibrary.utils.GlideEngine
import com.xly.middlelibrary.utils.LYUtils

class UserInfoFourthStepActivity : LYBaseActivity<ActivityUserInfoFourthStepBinding, LoginViewModel>() {

    private val lifePhotoList = mutableListOf<String>()
    private var selfIntroduction: String = ""
    private lateinit var photoAdapter: LifePhotoAdapter4

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoFourthStepActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoFourthStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupLifePhotos()
        setupSelfIntroduction()
        updateNextButtonState()
    }

    override fun initOnClick() {
        // 下一步按钮
        viewBind.btnNext.setOnClickListener {
            submitInfo()
        }
        
        // 跳过按钮 - 直接进入首页
        viewBind.tvSkip.setOnClickListener {
            ActivityStackManager.startActivityAndClearStack(
                this,
                LYMainActivity::class.java
            )
        }
    }

    private fun setupLifePhotos() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewBind.rvLifePhotos.layoutManager = layoutManager
        
        photoAdapter = LifePhotoAdapter4(
            photos = lifePhotoList,
            onAddClick = { showLifePhotoPickerDialog() },
            onDeleteClick = { position ->
                lifePhotoList.removeAt(position)
                photoAdapter.notifyDataSetChanged()
                updateNextButtonState()
            }
        )
        viewBind.rvLifePhotos.adapter = photoAdapter
    }

    private fun setupSelfIntroduction() {
        viewBind.etSelfIntroduction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selfIntroduction = s?.toString()?.trim() ?: ""
                updateNextButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        
        // 监听焦点变化，失去焦点时清除焦点隐藏光标
        viewBind.etSelfIntroduction.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                view.clearFocus()
            }
        }
        
        // 点击其他地方时隐藏键盘和光标
        viewBind.root.setOnClickListener {
            viewBind.etSelfIntroduction.clearFocus()
            // 隐藏键盘
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(viewBind.etSelfIntroduction.windowToken, 0)
        }
    }

    private fun showLifePhotoPickerDialog() {
        val remainingSlots = 4 - lifePhotoList.size
        if (remainingSlots <= 0) {
            showToast("最多只能上传4张生活照")
            return
        }
        
        val dialog = AvatarPickerBottomSheetDialogFragment().apply {
            onCameraClick = {
                selectLifePhotoFromCamera()
            }
            onGalleryClick = {
                selectLifePhotoFromGallery()
            }
        }
        dialog.show(supportFragmentManager, "LifePhotoPickerDialog")
    }

    private fun selectLifePhotoFromCamera() {
        // 检查权限
        if (!LYUtils.checkStoragePermission(this)) {
            LYUtils.requestStoragePermission(this)
            return
        }
        if (!LYUtils.checkCameraPermission(this)) {
            LYUtils.requestCameraPermission(this)
            return
        }

        val remainingSlots = 4 - lifePhotoList.size
        if (remainingSlots <= 0) {
            showToast("最多只能上传4张生活照")
            return
        }

        // 使用PictureSelector拍照，启用裁剪
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .setCropEngine(com.xly.middlelibrary.utils.LifePhotoCropEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        val localMedia = result[0]
                        val filePath = localMedia?.availablePath
                        if (!filePath.isNullOrEmpty() && lifePhotoList.size < 4) {
                            lifePhotoList.add(filePath)
                            photoAdapter.notifyDataSetChanged()
                            updateNextButtonState()
                        }
                    }
                }

                override fun onCancel() {
                    // 用户取消拍照
                }
            })
    }

    private fun selectLifePhotoFromGallery() {
        // 检查权限
        if (!LYUtils.checkStoragePermission(this)) {
            LYUtils.requestStoragePermission(this)
            return
        }

        val remainingSlots = 4 - lifePhotoList.size
        if (remainingSlots <= 0) {
            showToast("最多只能上传4张生活照")
            return
        }

        // 使用PictureSelector选择图片，启用裁剪（可以多选，但每次选择一张后需要裁剪）
        // 注意：多选模式下，PictureSelector会为每张图片单独调用裁剪引擎
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.instance)
            .setMaxSelectNum(remainingSlots)
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .setCropEngine(com.xly.middlelibrary.utils.LifePhotoCropEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        for (media in result) {
                            val filePath = media?.availablePath
                            if (!filePath.isNullOrEmpty() && lifePhotoList.size < 4) {
                                lifePhotoList.add(filePath)
                            }
                        }
                        photoAdapter.notifyDataSetChanged()
                        updateNextButtonState()
                    }
                }

                override fun onCancel() {
                    // 用户取消选择
                }
            })
    }

    private fun updateNextButtonState() {
        // 个人介绍必填，生活照可选（但建议至少上传）
        val isValid = selfIntroduction.isNotEmpty()
        
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
        viewModel.lifePhotos = lifePhotoList
        viewModel.selfIntroduction = selfIntroduction

        // 创建第四步请求实体
        val fourthStepRequest = UserInfoFourthStepRequest().apply {
            this.lifePhotos = this@UserInfoFourthStepActivity.lifePhotoList.toList()
            this.selfIntroduction = this@UserInfoFourthStepActivity.selfIntroduction
        }

        // 同时更新到 UserInfoRegisterReq（用于兼容现有接口）
        val request = UserInfoRegisterReq().apply {
            step = 4
            // 将图片路径列表转换为字符串（用逗号分隔）
            this.lifePhotos = fourthStepRequest.getLifePhotosString()
            this.selfIntroduction = fourthStepRequest.selfIntroduction
        }

        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (selfIntroduction.isEmpty()) {
            Toast.makeText(this, "请输入个人介绍", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lifePhotoList.size > 4) {
            Toast.makeText(this, "最多只能上传4张生活照", Toast.LENGTH_SHORT).show()
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
            
            // 跳转到第五步
            UserInfoFifthStepActivity.start(this)
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

