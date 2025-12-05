package com.xly.business.login.view

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.ActivityStackManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.model.UserInfoThirdStepRequest
import com.xly.business.login.model.UserInfoRegisterReq
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserInfoThirdStepBinding
import com.xly.index.LYMainActivity

class UserInfoThirdStepActivity : LYBaseActivity<ActivityUserInfoThirdStepBinding, LoginViewModel>() {

    private var houseStatus: String? = null
    private var carStatus: String? = null
    private var maritalStatus: String? = null
    private var childrenStatus: String? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInfoThirdStepActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater) = 
        ActivityUserInfoThirdStepBinding.inflate(layoutInflater)

    override fun initViewModel() = ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        setupHouse()
        setupCar()
        setupMarital()
        setupChildren()
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

    private fun setupHouse() {
        viewBind.llHouse.setOnClickListener {
            showHousePicker()
        }
    }

    private fun setupCar() {
        viewBind.llCar.setOnClickListener {
            showCarPicker()
        }
    }

    private fun setupMarital() {
        viewBind.llMarital.setOnClickListener {
            showMaritalPicker()
        }
    }

    private fun setupChildren() {
        viewBind.llChildren.setOnClickListener {
            showChildrenPicker()
        }
    }

    private fun showHousePicker() {
        val houseOptions = listOf(
            "有房产", "无房产"
        )
        BottomPickerDialog(
            this,
            "选择房产情况",
            houseOptions
        ) { selected ->
            houseStatus = selected
            viewBind.tvHouse.text = selected
            viewBind.tvHouse.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showCarPicker() {
        val carOptions = listOf(
            "有车", "无车"
        )
        BottomPickerDialog(
            this,
            "选择车辆情况",
            carOptions
        ) { selected ->
            carStatus = selected
            viewBind.tvCar.text = selected
            viewBind.tvCar.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showMaritalPicker() {
        val maritalOptions = listOf(
            "未婚", "离异", "丧偶"
        )
        BottomPickerDialog(
            this,
            "选择婚姻情况",
            maritalOptions
        ) { selected ->
            maritalStatus = selected
            viewBind.tvMarital.text = selected
            viewBind.tvMarital.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun showChildrenPicker() {
        val childrenOptions = listOf(
            "无子女", "有子女，和我一起生活", "有子女，不和我一起生活"
        )
        BottomPickerDialog(
            this,
            "选择子女情况",
            childrenOptions
        ) { selected ->
            childrenStatus = selected
            viewBind.tvChildren.text = selected
            viewBind.tvChildren.setTextColor(getColor(R.color.text_primary))
            updateNextButtonState()
        }.show()
    }

    private fun updateNextButtonState() {
        // 所有字段必填
        val isValid = houseStatus != null && 
                     carStatus != null &&
                     maritalStatus != null &&
                     childrenStatus != null
        
        viewBind.btnNext.isEnabled = isValid
    }

    private fun submitInfo() {
        if (!validateInput()) {
            return
        }

        // 保存到 ViewModel
        viewModel.houseStatus = houseStatus
        viewModel.carStatus = carStatus
        viewModel.maritalStatus = maritalStatus
        viewModel.childrenStatus = childrenStatus

        // 创建第三步请求实体
        val thirdStepRequest = UserInfoThirdStepRequest().apply {
            this.houseStatus = this@UserInfoThirdStepActivity.houseStatus ?: ""
            this.carStatus = this@UserInfoThirdStepActivity.carStatus ?: ""
            this.maritalStatus = this@UserInfoThirdStepActivity.maritalStatus ?: ""
            this.childrenStatus = this@UserInfoThirdStepActivity.childrenStatus ?: ""
        }

        // 同时更新到 UserInfoRegisterReq（用于兼容现有接口）
        val request = UserInfoRegisterReq().apply {
            step = 3
            this.houseStatus = thirdStepRequest.houseStatus
            this.carStatus = thirdStepRequest.carStatus
            this.maritalStatus = thirdStepRequest.maritalStatus
            this.childrenStatus = thirdStepRequest.childrenStatus
        }

        showLoading()
        viewModel.userInfoRegister(request)
    }

    private fun validateInput(): Boolean {
        if (houseStatus == null) {
            Toast.makeText(this, "请选择房产情况", Toast.LENGTH_SHORT).show()
            return false
        }
        if (carStatus == null) {
            Toast.makeText(this, "请选择车辆情况", Toast.LENGTH_SHORT).show()
            return false
        }
        if (maritalStatus == null) {
            Toast.makeText(this, "请选择婚姻情况", Toast.LENGTH_SHORT).show()
            return false
        }
        if (childrenStatus == null) {
            Toast.makeText(this, "请选择子女情况", Toast.LENGTH_SHORT).show()
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
        viewModel.houseStatus = houseStatus
        viewModel.carStatus = carStatus
        viewModel.maritalStatus = maritalStatus
        viewModel.childrenStatus = childrenStatus
        
        // 模拟网络请求延迟（1.5秒）
        Handler(Looper.getMainLooper()).postDelayed({
            // 恢复按钮状态
            viewBind.progressBar.visibility = android.view.View.GONE
            viewBind.ivArrow.visibility = android.view.View.VISIBLE
            viewBind.btnNext.isEnabled = true
            viewBind.btnNext.isClickable = true
            
            // 跳转到第四步
            UserInfoFourthStepActivity.start(this)
            finish()
        }, 1500)
    }

    override fun initObservers() {
        viewModel.registerResult.observe(this) { authResponse ->
            hideLoading()
            // 跳转到第四步
            UserInfoFourthStepActivity.start(this)
            finish()
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            hideLoading()
            showToast(errorMessage)
        }
    }
}

