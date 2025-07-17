package com.xly.business.find.view

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class MomentImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewPager = ViewPager2(this)
        setContentView(viewPager)

        val images = intent.getIntArrayExtra("imageList") ?: intArrayOf()
        val index = intent.getIntExtra("index", 0)
        val momentId = intent.getStringExtra("momentId") ?: ""

        viewPager.adapter = object : RecyclerView.Adapter<ImageViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                // 使用 PhotoView 支持手势缩放和滑动
                val photoView = PhotoView(parent.context)
                photoView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                return ImageViewHolder(photoView)
            }

            override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                val imageResId = images[position]
                holder.photoView.transitionName = "moment_image_${momentId}_$position"
                Glide.with(holder.photoView).load(imageResId).into(holder.photoView)

                // 添加点击关闭功能
                holder.photoView.setOnClickListener {
                    finish()
                }
            }

            override fun getItemCount(): Int = images.size
        }
        viewPager.setCurrentItem(index, false)
    }

    class ImageViewHolder(val photoView: PhotoView) : RecyclerView.ViewHolder(photoView)
}