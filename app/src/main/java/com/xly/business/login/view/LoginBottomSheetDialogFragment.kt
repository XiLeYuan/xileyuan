package com.xly.business.login.view

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Outline
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.DecelerateInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xly.databinding.LayoutLoginBottomSheetBinding

class LoginBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: LayoutLoginBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onLoginClick: (() -> Unit)? = null
    var isAgreeChecked: () -> Boolean = { binding.cbAgree.isChecked }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutLoginBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始状态：透明且向下偏移
        view.alpha = 0f
        view.translationY = 300f
        
        // 在布局完成后设置圆角裁剪
        view.post {
            setupRoundedCorners()
            // 延迟一帧后开始动画
            view.post {
                animateDialogIn(view)
            }
        }
        
        binding.btnLogin.setOnClickListener {
            onLoginClick?.invoke()
        }
        binding.tvUserProtocol.setOnClickListener {
            val intent = Intent(requireContext(), com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "用户协议")
            intent.putExtra("url", "https://yourdomain.com/user_protocol")
            startActivity(intent)
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireContext(), com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "隐私政策")
            intent.putExtra("url", "https://yourdomain.com/privacy_policy")
            startActivity(intent)
        }
        binding.tvUserProtocol.paint.isUnderlineText = true
        binding.tvPrivacyPolicy.paint.isUnderlineText = true
    }
    
    private fun setupRoundedCorners() {
        // 24dp 转换为像素
        val cornerRadius = 24 * resources.displayMetrics.density
        binding.root.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    // 使用 Path 来只设置顶部两个角的圆角
                    val path = Path().apply {
                        moveTo(0f, cornerRadius)
                        quadTo(0f, 0f, cornerRadius, 0f)
                        lineTo(view.width - cornerRadius, 0f)
                        quadTo(view.width.toFloat(), 0f, view.width.toFloat(), cornerRadius)
                        lineTo(view.width.toFloat(), view.height.toFloat())
                        lineTo(0f, view.height.toFloat())
                        close()
                    }
                    outline.setConvexPath(path)
                }
            }
        }
    }
    
    private fun animateDialogIn(view: View) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 450
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                view.alpha = progress
                view.translationY = 300f * (1 - progress)
            }
            start()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            // 彻底移除默认的白色背景，让我们的圆角背景显示出来
            it.background = null
            it.setBackgroundDrawable(null)
            
            // 确保可以裁剪圆角
            binding.root.clipToOutline = true
            
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
        
        // 也设置 window 背景为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 