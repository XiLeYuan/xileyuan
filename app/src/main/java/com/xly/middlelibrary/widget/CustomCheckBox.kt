package com.xly.middlelibrary.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.xly.R

/**
 * 自定义CheckBox组件
 * 使用main_radio_normal和main_radio_selecte图标
 * 支持选中/未选中状态切换，带动画效果
 */
class CustomCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var isChecked: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                updateIcon()
                // 触发选中状态变化监听
                onCheckedChangeListener?.invoke(this, value)
            }
        }

    var onCheckedChangeListener: ((CustomCheckBox, Boolean) -> Unit)? = null

    init {
        // 设置为可点击
        isClickable = true
        isFocusable = true
        
        // 设置初始图标
        updateIcon()
        
        // 设置点击监听
        setOnClickListener {
            toggle()
        }
    }

    /**
     * 获取选中状态
     */
    fun isChecked(): Boolean = isChecked

    /**
     * 设置选中状态（带动画）
     */
    fun setChecked(checked: Boolean, animate: Boolean = true) {
        if (isChecked == checked) return
        
        if (animate) {
            animateToggle()
        } else {
            isChecked = checked
        }
    }

    /**
     * 切换选中状态（带动画）
     */
    fun toggle() {
        setChecked(!isChecked, animate = true)
    }

    /**
     * 更新图标
     */
    private fun updateIcon() {
        val iconRes = if (isChecked) {
            R.mipmap.main_radio_selecte
        } else {
            R.mipmap.main_radio_normal
        }
        setImageResource(iconRes)
    }

    /**
     * 切换动画效果
     * 设计：缩放+淡入淡出+弹性效果
     */
    private fun animateToggle() {
        val animatorSet = AnimatorSet()
        
        // 第一阶段：缩小并淡出（0.8倍 + 透明度0.5）
        val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.8f)
        val fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.5f)
        
        val shrinkAnim = AnimatorSet().apply {
            duration = 150
            interpolator = DecelerateInterpolator()
            playTogether(scaleDownX, scaleDownY, fadeOut)
        }
        
        // 第二阶段：更新图标（在动画中间）
        shrinkAnim.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // 在缩小动画结束时更新图标
                isChecked = !isChecked
            }
        })
        
        // 第三阶段：放大并淡入（1.0倍 + 弹性效果 + 透明度1.0）
        val scaleUpX = ObjectAnimator.ofFloat(this, "scaleX", 0.8f, 1.15f, 1.0f)
        val scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 0.8f, 1.15f, 1.0f)
        val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1.0f)
        
        val expandAnim = AnimatorSet().apply {
            duration = 200
            interpolator = OvershootInterpolator(1.2f) // 弹性效果
            playTogether(scaleUpX, scaleUpY, fadeIn)
        }
        
        // 组合动画
        animatorSet.playSequentially(shrinkAnim, expandAnim)
        animatorSet.start()
    }
}

