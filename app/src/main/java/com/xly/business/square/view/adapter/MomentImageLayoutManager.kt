package com.xly.business.square.view.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout

/**
 * 动态图片布局管理器
 * 根据图片数量自动选择最佳的布局样式
 */
object MomentImageLayoutManager {

    // 最大显示图片数量
    private const val MAX_DISPLAY_COUNT = 3

    /**
     * 图片尺寸配置
     */
    data class ImageSize(
        val width: Int,
        val height: Int
    )

    /**
     * 根据图片数量获取布局配置
     */
    fun getLayoutConfig(
        context: Context,
        imageCount: Int,
        screenWidth: Int,
        isVertical: Boolean = false
    ): List<ImageSize> {
        val displayCount = imageCount.coerceAtMost(MAX_DISPLAY_COUNT)
        val padding = 68.dpToPx(context) // 左边距
        val margin = 2.dpToPx(context) // 图片间距（减小）
        val availableWidth = screenWidth - padding - 32.dpToPx(context) // 可用宽度（减去右边距）
        
        return when (displayCount) {
            1 -> getSingleImageLayout(availableWidth, isVertical, margin)
            2 -> getTwoImagesLayout(availableWidth, margin)
            3 -> getThreeImagesLayout(availableWidth, margin)
            else -> emptyList()
        }
    }

    /**
     * 单张图片：大图显示，可以是横图或竖图
     * 支持两种类型：
     * - 横图：4:3 比例（width * 0.75），宽度占满可用宽度
     * - 竖图：3:4 比例（width * 1.33），宽度为屏幕的一半，高度同比例缩放
     */
    fun getSingleImageLayout(availableWidth: Int, isVertical: Boolean = false, margin: Int = 0): List<ImageSize> {
        val width: Int
        val height: Int
        
        if (isVertical) {
            // 竖图：宽度为屏幕的一半
            width = availableWidth / 2
            // 高度按3:4比例缩放（高大于宽）
            height = (width * 1.33f).toInt() // 3:4 比例，竖图
        } else {
            // 横图：宽度占满可用宽度
            width = availableWidth
            height = (width * 0.75f).toInt() // 4:3 比例，横图
        }
        return listOf(ImageSize(width, height))
    }

    /**
     * 2张图片：并排显示，正方形
     */
    private fun getTwoImagesLayout(availableWidth: Int, margin: Int): List<ImageSize> {
        val size = (availableWidth - margin) / 2
        return listOf(
            ImageSize(size, size),
            ImageSize(size, size)
        )
    }

    /**
     * 3张图片：第一张最大，高度等于第二三张之和，宽度等于第二三张之和
     * 第二张和第三张并列布局（上下排列）
     */
    private fun getThreeImagesLayout(availableWidth: Int, margin: Int): List<ImageSize> {
        // 第二张和第三张的宽度（相等）
        val secondThirdWidth = (availableWidth - margin * 2) / 3
        // 第二张和第三张的高度（相等）
        val secondThirdHeight = secondThirdWidth
        
        // 第一张的宽度 = 第二张宽度 + 第三张宽度 + 间距
        val firstWidth = secondThirdWidth * 2 + margin
        // 第一张的高度 = 第二张高度 + 第三张高度 + 间距
        val firstHeight = secondThirdHeight * 2 + margin
        
        return listOf(
            ImageSize(firstWidth, firstHeight), // 第一张：最大
            ImageSize(secondThirdWidth, secondThirdHeight), // 第二张
            ImageSize(secondThirdWidth, secondThirdHeight) // 第三张
        )
    }


    /**
     * 检查是否需要显示"更多"标识
     */
    fun shouldShowMoreIndicator(imageCount: Int): Boolean {
        return imageCount > MAX_DISPLAY_COUNT
    }

    /**
     * 获取实际显示的图片数量
     */
    fun getDisplayCount(imageCount: Int): Int {
        return imageCount.coerceAtMost(MAX_DISPLAY_COUNT)
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}

