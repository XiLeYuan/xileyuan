package com.xly.business.login.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.xly.databinding.LayoutLoginBottomSheetBinding

class LoginDialog(context: Context) : Dialog(context) {
    
    private lateinit var binding: LayoutLoginBottomSheetBinding
    var onLoginClick: (() -> Unit)? = null
    var isAgreeChecked: () -> Boolean = { binding.cbAgree.isChecked() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置窗口样式
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            
            // 设置窗口动画
            attributes.windowAnimations = android.R.style.Animation_Translucent
        }
        
        binding = LayoutLoginBottomSheetBinding.inflate(layoutInflater)
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
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.BOTTOM
            attributes = params
        }
        
        // 设置初始状态并开始动画
        setupInitialState()
        binding.root.post {
            animateDialogIn()
        }
    }
    
    private fun setupInitialState() {
        // 弹窗容器初始状态
        binding.root.apply {
            alpha = 0f
            translationY = 400f
        }
        
        // 内容初始状态（透明且稍微向上偏移）
        binding.btnLogin.apply {
            alpha = 0f
            translationY = 30f
        }
        
        binding.cbAgree.parent?.let { agreementLayout ->
            (agreementLayout as? ViewGroup)?.apply {
                alpha = 0f
                translationY = 20f
            }
        }
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            onLoginClick?.invoke()
        }
        binding.tvUserProtocol.setOnClickListener {
            val intent = Intent(context, com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "用户协议")
            intent.putExtra("url", "https://yourdomain.com/user_protocol")
            context.startActivity(intent)
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(context, com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "隐私政策")
            intent.putExtra("url", "https://yourdomain.com/privacy_policy")
            context.startActivity(intent)
        }
        binding.tvUserProtocol.paint.isUnderlineText = true
        binding.tvPrivacyPolicy.paint.isUnderlineText = true
    }
    
    private fun animateDialogIn() {
        // 弹窗容器动画：从底部滑入 + 淡入
        val containerAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f).apply {
                    duration = 400
                },
                ObjectAnimator.ofFloat(binding.root, "translationY", 400f, 0f).apply {
                    duration = 500
                    interpolator = OvershootInterpolator(0.8f) // 轻微回弹效果，更丝滑
                }
            )
        }
        
        // 按钮动画：延迟出现，向上滑入 + 淡入
        val buttonAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.btnLogin, "alpha", 0f, 1f).apply {
                    duration = 300
                    startDelay = 200 // 延迟200ms，等弹窗滑入一部分后再出现
                },
                ObjectAnimator.ofFloat(binding.btnLogin, "translationY", 30f, 0f).apply {
                    duration = 350
                    startDelay = 200
                    interpolator = DecelerateInterpolator()
                }
            )
        }
        
        // 协议文字动画：延迟出现，向上滑入 + 淡入
        val agreementLayout = binding.cbAgree.parent as? ViewGroup
        val agreementAnimator = AnimatorSet().apply {
            if (agreementLayout != null) {
                playTogether(
                    ObjectAnimator.ofFloat(agreementLayout, "alpha", 0f, 1f).apply {
                        duration = 300
                        startDelay = 300 // 延迟300ms
                    },
                    ObjectAnimator.ofFloat(agreementLayout, "translationY", 20f, 0f).apply {
                        duration = 350
                        startDelay = 300
                        interpolator = DecelerateInterpolator()
                    }
                )
            }
        }
        
        // 按顺序播放动画
        AnimatorSet().apply {
            play(containerAnimator)
            play(buttonAnimator).after(0)
            play(agreementAnimator).after(0)
            start()
        }
    }
}
