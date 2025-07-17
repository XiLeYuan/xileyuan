package com.xly.business.find.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.xly.middlelibrary.widget.DragPhotoView

class MomentImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dragPhotoView = DragPhotoView(this)
        setContentView(dragPhotoView)

        val imageResId = intent.getIntExtra("imageResId", 0)
        val momentId = intent.getStringExtra("momentId") ?: ""
        val imageIndex = intent.getIntExtra("imageIndex", 0)

        // 正确获取 PhotoView
        val photoView = dragPhotoView.getPhotoView()

        // 设置转场名称
        photoView.transitionName = "moment_image_${momentId}_$imageIndex"

        // 加载图片
        Glide.with(photoView).load(imageResId).into(photoView)

        // 设置下拉关闭监听
        dragPhotoView.setOnDragListener(object : DragPhotoView.OnDragListener {
            override fun onDragClose() {
                supportFinishAfterTransition()
            }
        })
        dragPhotoView.getPhotoView().setOnClickListener {
            supportFinishAfterTransition()
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