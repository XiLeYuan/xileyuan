package com.xly.middlelibrary.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View

class BubbleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var label: String = "标签"
        set(value) { field = value; invalidate() }

    private val density = resources.displayMetrics.density
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // 颜色配置
    private val normalCenter = Color.parseColor("#2a2a3a") // 中心深色
    private val normalEdge = Color.parseColor("#0E0F1A") // 边缘更深
    private val selectedCenter = Color.parseColor("#BFE7FF") // 选中亮
    private val selectedEdge = Color.parseColor("#6FAEFF") // 选中边缘
    private val strokeColor = Color.argb(140, 255, 255, 255)

    // 进度 0..1, 用于选中态动画插值
    private var selProgress = 0f

    private val argbEvaluator = ArgbEvaluator()

    init {
        try {
            setLayerType(LAYER_TYPE_SOFTWARE, null) // 需要 BlurMaskFilter 生效
            textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
            textPaint.color = Color.parseColor("#D8E8FF")
            strokePaint.strokeWidth = 2f * density
        } catch (e: Exception) {
            android.util.Log.e("BubbleView", "Failed to initialize BubbleView", e)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            // 通常adapter会给定 100dp 大小，这里兼容性测量
            val default = (120 * density).toInt()
            val w = resolveSize(default, widthMeasureSpec)
            val h = resolveSize(default, heightMeasureSpec)
            setMeasuredDimension(w, h)
        } catch (e: Exception) {
            Log.e("BubbleView", "Failed to measure BubbleView", e)
            setMeasuredDimension(100, 100)
        }
    }

    override fun onDraw(canvas: Canvas) {
        try {
            super.onDraw(canvas)
            val cx = width / 2f
            val cy = height / 2f
            val radius = (width.coerceAtMost(height) / 2f) - 6f * density

            // 计算过渡颜色
            val centerColor = argbEvaluator.evaluate(selProgress, normalCenter, selectedCenter) as Int
            val edgeColor = argbEvaluator.evaluate(selProgress, normalEdge, selectedEdge) as Int
            val glowAlpha = (60 + 140 * selProgress).toInt().coerceAtMost(220)

            // 外发光
            glowPaint.color = centerColor
            glowPaint.maskFilter = BlurMaskFilter(18f * density, BlurMaskFilter.Blur.NORMAL)
            glowPaint.alpha = glowAlpha
            canvas.drawCircle(cx, cy, radius + 12f * density, glowPaint)

            // 主体径向渐变
            circlePaint.shader = RadialGradient(
                cx, cy - radius * 0.15f, radius,
                intArrayOf(centerColor, edgeColor), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy, radius, circlePaint)

            // 边框 (半透明)
            strokePaint.color = strokeColor
            canvas.drawCircle(cx, cy, radius - strokePaint.strokeWidth/2f, strokePaint)

            // 文字
            textPaint.color = if (selProgress > 0.6f) Color.WHITE else Color.parseColor("#C8D6E6")
            // add a little inner text shadow for readability
            textPaint.setShadowLayer(6f * (0.6f + selProgress), 0f, 0f, Color.argb((80 + 120 * selProgress).toInt(),255,255,255))
            val y = cy - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(label, cx, y, textPaint)
        } catch (e: Exception) {
            Log.e("BubbleView", "Failed to draw BubbleView", e)
        }
    }

    fun setSelectedAnimated(selected: Boolean) {
        try {
            val from = selProgress
            val to = if (selected) 1f else 0f
            ValueAnimator.ofFloat(from, to).apply {
                duration = 280
                addUpdateListener {
                    selProgress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("BubbleView", "Failed to animate selection", e)
            // 如果动画失败，直接设置状态
            instantSelect(selected)
        }
    }

    fun instantSelect(selected: Boolean) {
        try {
            selProgress = if (selected) 1f else 0f
            invalidate()
        } catch (e: Exception) {
            Log.e("BubbleView", "Failed to instant select", e)
        }
    }
}