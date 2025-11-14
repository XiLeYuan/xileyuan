package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.business.user.view.adapter.VerificationAdapter
import com.xly.databinding.ActivityVerificationBinding

class VerificationActivity : LYBaseActivity<ActivityVerificationBinding, LoginViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VerificationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupRecyclerView()
    }

    override fun initView() {
        super.initView()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityVerificationBinding {
        return ActivityVerificationBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): LoginViewModel {
        return ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private fun setupToolbar() {
        viewBind.ivBack.setOnClickListener {
            finish()
        }
    }




    private fun setupRecyclerView() {
        val verificationTypes = listOf(
            VerificationType(
                title = "实名认证",
                description = "上传身份证进行实名认证，提高账号可信度",
                iconRes = android.R.drawable.ic_menu_myplaces, // 临时图标，后续可替换
                isVerified = false
            ),
            VerificationType(
                title = "头像认证",
                description = "上传真实头像照片，通过人脸识别验证",
                iconRes = android.R.drawable.ic_menu_camera, // 临时图标，后续可替换
                isVerified = false
            ),
            VerificationType(
                title = "学历认证",
                description = "上传学历证书，验证教育背景真实性",
                iconRes = android.R.drawable.ic_menu_agenda, // 临时图标，后续可替换
                isVerified = false
            )
        )

        viewBind.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBind.recyclerView.adapter = VerificationAdapter(verificationTypes) { verificationType ->
            // TODO: 处理认证卡片点击事件
            when (verificationType.title) {
                "实名认证" -> {
                    // TODO: 跳转到实名认证页面
                }
                "头像认证" -> {
                    // TODO: 跳转到头像认证页面
                }
                "学历认证" -> {
                    // TODO: 跳转到学历认证页面
                }
            }
        }
    }

    data class VerificationType(
        val title: String,
        val description: String,
        val iconRes: Int,
        val isVerified: Boolean
    )
}

