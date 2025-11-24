package com.xly.business.login.view

import android.graphics.Outline
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xly.databinding.DialogAvatarPickerBottomSheetBinding

class AvatarPickerBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogAvatarPickerBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onCameraClick: (() -> Unit)? = null
    var onGalleryClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAvatarPickerBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.llCamera.setOnClickListener {
            onCameraClick?.invoke()
            dismiss()
        }

        binding.llGallery.setOnClickListener {
            onGalleryClick?.invoke()
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            // 移除默认背景
            it.background = null
            it.setBackgroundDrawable(null)
            
            // 设置圆角（左上角和右上角24dp）
            it.post {
                val cornerRadius = 24 * resources.displayMetrics.density
                it.clipToOutline = true
                it.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        if (view.width > 0 && view.height > 0) {
                            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                        }
                    }
                }
            }
            
            // 确保可以裁剪圆角
            binding.root.clipToOutline = true
            
            // 设置可拖拽关闭
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = true
        }
        
        // 设置窗口背景为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

