package com.xly.business.find.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.middlelibrary.widget.DragPhotoView
import com.xly.databinding.ActivityImageDetailBinding

class MomentImageDetailActivity : LYBaseActivity<ActivityImageDetailBinding, RecommendViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageResId = intent.getIntExtra("imageResId", 0)
        val momentId = intent.getStringExtra("momentId") ?: ""
        val imageIndex = intent.getIntExtra("imageIndex", 0)

        // 正确获取 PhotoView
        val photoView = viewBind.dragPhotoView.getPhotoView()

        // 设置转场名称
        photoView.transitionName = "moment_image_${momentId}_$imageIndex"

        // 加载图片
        Glide.with(photoView).load(imageResId).into(photoView)

        // 设置下拉关闭监听
        viewBind.dragPhotoView.setOnDragListener(object : DragPhotoView.OnDragListener {
            override fun onDragClose() {
                supportFinishAfterTransition()
            }
        })
        viewBind.dragPhotoView.getPhotoView().setOnClickListener {
            supportFinishAfterTransition()
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityImageDetailBinding {
        return ActivityImageDetailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(this)[RecommendViewModel::class.java]
    }

    private fun finishWithTransition() {
        // 手动触发转场动画
        supportFinishAfterTransition()
    }

    override fun onBackPressed() {
        finishWithTransition()
    }
}