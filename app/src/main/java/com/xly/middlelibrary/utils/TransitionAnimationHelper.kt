package com.xly.middlelibrary.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * 淡出卡片上的非图片元素，用于转场动画前的过渡
 * @param views 需要淡出的视图列表
 * @param duration 动画时长（毫秒），默认200ms
 * @param onAnimationEnd 动画结束回调
 */
fun fadeOutViews(
    views: List<View>,
    duration: Long = 200,
    onAnimationEnd: (() -> Unit)? = null
) {
    if (views.isEmpty()) {
        onAnimationEnd?.invoke()
        return
    }

    var completedCount = 0
    val totalCount = views.size

    views.forEach { view ->
        if (view.visibility == View.VISIBLE && view.alpha > 0f) {
            view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        completedCount++
                        if (completedCount == totalCount) {
                            onAnimationEnd?.invoke()
                        }
                    }
                })
                .start()
        } else {
            completedCount++
            if (completedCount == totalCount) {
                onAnimationEnd?.invoke()
            }
        }
    }
}

/**
 * 淡出ViewGroup中的所有子视图（递归）
 */
fun fadeOutViewGroupChildren(
    viewGroup: ViewGroup,
    duration: Long = 200,
    excludeViews: List<View> = emptyList()
) {
    val viewsToFade = mutableListOf<View>()
    
    fun collectViews(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child !in excludeViews) {
                if (child is ViewGroup) {
                    collectViews(child)
                } else if (child.visibility == View.VISIBLE) {
                    viewsToFade.add(child)
                }
            }
        }
    }
    
    collectViews(viewGroup)
    fadeOutViews(viewsToFade, duration)
}

/**
 * 恢复视图的alpha值到1.0，用于从详情页返回时恢复卡片元素
 * @param views 需要恢复的视图列表
 * @param duration 动画时长（毫秒），默认200ms
 */
fun fadeInViews(
    views: List<View>,
    duration: Long = 200
) {
    views.forEach { view ->
        if (view.visibility == View.VISIBLE && view.alpha < 1f) {
            view.animate()
                .alpha(1f)
                .setDuration(duration)
                .start()
        }
    }
}

