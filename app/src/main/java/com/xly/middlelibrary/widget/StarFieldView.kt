package com.xly.middlelibrary.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class StarFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint = Paint()
    private val stars = mutableListOf<Star>()
    private val numStars = 140
    private var initialized = false

    data class Star(var x: Float, var y: Float, var z: Float, var speed: Float)

    private fun initStars() {
        stars.clear()
        for (i in 0 until numStars) {
            stars.add(randomStar())
        }
        initialized = true
    }

    private fun randomStar(): Star {
        val w = width.coerceAtLeast(1)
        val h = height.coerceAtLeast(1)
        return Star(
            x = Random.nextFloat() * w,
            y = Random.nextFloat() * h,
            z = Random.nextFloat() * w,
            speed = 1f + Random.nextFloat() * 6f
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initStars()
        // 背景渐变（深蓝到近黑）
        bgPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h.toFloat(),
            intArrayOf(Color.rgb(6, 8, 20), Color.rgb(12, 13, 30)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(), bgPaint)

        val cx = width / 2f
        val cy = height / 2f

        for (s in stars) {
            s.z -= s.speed
            if (s.z <= 0) {
                val new = randomStar()
                s.x = new.x; s.y = new.y; s.z = width.toFloat(); s.speed = new.speed
            }
            val k = (width.toFloat() / s.z).coerceAtMost(1f)
            val sx = (s.x - cx) * k + cx
            val sy = (s.y - cy) * k + cy
            val radius = (1.0f - s.z / width) * 3.2f * (1f + s.speed/6f)
            paint.color = Color.WHITE
            paint.alpha = (80 + (175 * (1f - s.z / width))).toInt().coerceIn(30,255)
            canvas.drawCircle(sx, sy, radius, paint)
        }

        // 少量微弱星屑
        paint.alpha = 30
        paint.color = Color.WHITE
        for (i in 0 until 6) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            canvas.drawCircle(x, y, Random.nextFloat() * 1.6f, paint)
        }

        postInvalidateOnAnimation()
    }
}