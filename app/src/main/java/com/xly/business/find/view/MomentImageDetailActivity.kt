package com.xly.business.find.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class MomentImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 直接使用 PhotoView，不需要 ViewPager2
        val photoView = PhotoView(this)
        setContentView(photoView)

        val imageResId = intent.getIntExtra("imageResId", 0)
        val momentId = intent.getStringExtra("momentId") ?: ""
        val imageIndex = intent.getIntExtra("imageIndex", 0)

        // 设置转场名称
        photoView.transitionName = "moment_image_${momentId}_$imageIndex"

        // 加载图片
        Glide.with(photoView).load(imageResId).into(photoView)

        // 点击关闭
        photoView.setOnClickListener {
            finishWithTransition()
        }
    }

    private fun finishWithTransition() {
        // 手动触发转场动画
        supportFinishAfterTransition()
    }

    override fun onBackPressed() {
        finishWithTransition()
    }
}