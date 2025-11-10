package com.xly.business.login.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import com.xly.databinding.DialogPrivacyAgreementBinding
import com.xly.middlelibrary.utils.MMKVManager

class PrivacyAgreementDialog(context: Context) : Dialog(context) {
    
    private lateinit var binding: DialogPrivacyAgreementBinding
    var onAgreeClick: (() -> Unit)? = null
    var onDisagreeClick: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置窗口样式
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            
            // 设置窗口动画
            attributes.windowAnimations = android.R.style.Animation_Dialog
        }
        
        binding = DialogPrivacyAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置不可取消
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        
        setupViews()
    }
    
    override fun onStart() {
        super.onStart()
        // 设置窗口宽度和位置
        window?.apply {
            val params = attributes
            params.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            // 添加阴影效果
            params.dimAmount = 0.5f
            // 启用窗口阴影
            setFlags(
                android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
            attributes = params
        }
    }
    
    private fun setupViews() {
        // 同意按钮
        binding.btnAgree.setOnClickListener {
            // 保存同意状态
            MMKVManager.putBoolean(MMKVManager.KEY_PRIVACY_AGREED, true)
            onAgreeClick?.invoke()
            dismiss()
        }
        
        // 不同意按钮
        binding.btnDisagree.setOnClickListener {
            onDisagreeClick?.invoke()
            // 不同意时退出应用
            (context as? android.app.Activity)?.finish()
        }
    }
}

