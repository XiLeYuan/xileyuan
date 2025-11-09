package com.xly.business.square.view.adapter

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.xly.middlelibrary.utils.LYUtils
import java.security.MessageDigest

/**
 * Glide 模糊转换
 */
class BlurTransformation(
    private val context: Context,
    private val radius: Float = 25f
) : BitmapTransformation() {

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        // 先缩小图片以提高模糊性能
        val scaledWidth = toTransform.width / 4
        val scaledHeight = toTransform.height / 4
        val scaledBitmap = Bitmap.createScaledBitmap(
            toTransform,
            scaledWidth,
            scaledHeight,
            true
        )
        // 创建新的bitmap用于模糊，避免修改原始bitmap
        val blurInput = scaledBitmap.copy(scaledBitmap.config, true)
        // 应用模糊
        val blurredBitmap = LYUtils.blurBitmap(context, blurInput, radius)
        // 放大回原始尺寸
        val result = Bitmap.createScaledBitmap(
            blurredBitmap,
            toTransform.width,
            toTransform.height,
            true
        )
        // 回收临时bitmap
        if (scaledBitmap != blurInput) {
            scaledBitmap.recycle()
        }
        if (blurredBitmap != result) {
            blurredBitmap.recycle()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        return other is BlurTransformation && other.radius == radius
    }

    override fun hashCode(): Int {
        return radius.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("BlurTransformation(radius=$radius)".toByteArray())
    }
}

