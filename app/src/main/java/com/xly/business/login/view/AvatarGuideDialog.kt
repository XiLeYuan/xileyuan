package com.xly.business.login.view

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xly.databinding.DialogAvatarGuideBinding

class AvatarGuideDialog(
    context: Context,
    private val onGoSelectPhoto: () -> Unit
) : BottomSheetDialog(context) {

    private val binding: DialogAvatarGuideBinding

    init {
        binding = DialogAvatarGuideBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        
        // 设置弹窗高度为屏幕的三分之二
        val displayMetrics = context.resources.displayMetrics
        val height = (displayMetrics.heightPixels * 0.67).toInt()
        window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
        
        // 设置不可拖拽和点击外部关闭
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        
        // 设置背景为40%黑色透明度
        window?.setDimAmount(0.4f)
        // 确保背景变暗效果正确应用
        window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        
        binding.btnGoSelectPhoto.setOnClickListener {
            dismiss()
            onGoSelectPhoto()
        }
    }
}