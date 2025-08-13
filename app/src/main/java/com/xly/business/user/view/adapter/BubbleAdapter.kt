package com.xly.business.user.view.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.xly.R
import com.xly.middlelibrary.widget.BubbleView

data class BubbleItem(val text: String, var selected: Boolean = false)

class BubbleAdapter(
    private val ctx: Context,
    private val items: MutableList<BubbleItem>,
    private val onSelectionChanged: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<BubbleAdapter.VH>() {

    // ViewHolder 使用 container 作为 itemView，内部持有 bubble 引用
    inner class VH(val container: FrameLayout, val bubble: BubbleView) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 120f, parent.resources.displayMetrics
        ).toInt()
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, parent.resources.displayMetrics
        ).toInt()

        // 外层 container（将作为 itemView 返回）
        val container = FrameLayout(parent.context)
        // 使用 FlexboxLayoutManager.LayoutParams 而不是 MarginLayoutParams
        val lp = FlexboxLayoutManager.LayoutParams(sizePx, sizePx)
        lp.setMargins(margin / 2, margin / 2, margin / 2, margin / 2)
        container.layoutParams = lp

        // 允许子 view 的发光效果不被剪裁
        container.clipToPadding = false
        container.clipChildren = false

        // Bubble 占满 container
        val bubble = BubbleView(parent.context)
        val bubbleLp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bubble.layoutParams = bubbleLp

        container.addView(bubble)

        return VH(container, bubble)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.bubble.label = item.text
        holder.bubble.instantSelect(item.selected)

        holder.bubble.setOnClickListener {
            item.selected = !item.selected
            holder.bubble.setSelectedAnimated(item.selected)
            if (item.selected) {
                // 随机五彩：用渐变变色通过外层动画（简单做法：立即改变选中态后再启动一次微小缩放）
                holder.bubble.animate().scaleX(1.05f).scaleY(1.05f).setDuration(120)
                    .withEndAction {
                        holder.bubble.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                    }.start()
            }
            
            // 通知选择状态变化
            onSelectionChanged?.invoke(items.count { it.selected })
        }

        // 避免重复添加动画，使用 container 的 tag 存标识
        if (holder.container.getTag(R.id.bubble_anim_tag) != true) {
            startBubbleAnimation(holder.container)
            holder.container.setTag(R.id.bubble_anim_tag, true)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun dpToPx(dp: Int): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), ctx.resources.displayMetrics)

    private fun startBubbleAnimation(v: View) {
        try {
            val translate = ObjectAnimator.ofFloat(v, "translationX", -dpToPx(4), dpToPx(4)).apply {
                duration = (3000..5200).random().toLong()
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
            val scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.045f).apply {
                duration = (2200..4000).random().toLong()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = LinearInterpolator()
            }
            val scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.045f).apply {
                duration = (2200..4000).random().toLong()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = LinearInterpolator()
            }
            AnimatorSet().apply {
                playTogether(translate, scaleX, scaleY)
                start()
            }
        } catch (e: Exception) {
            // 如果动画启动失败，记录日志但不崩溃
            Log.e("BubbleAdapter", "Failed to start bubble animation", e)
        }
    }
}