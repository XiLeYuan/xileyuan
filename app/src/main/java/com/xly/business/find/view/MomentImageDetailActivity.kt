package com.xly.business.find.view

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide

class MomentImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewPager = ViewPager2(this)
        setContentView(viewPager)

        val images = intent.getStringArrayListExtra("imageList") ?: arrayListOf()
        val index = intent.getIntExtra("index", 0)
        val momentId = intent.getStringExtra("momentId") ?: ""

        viewPager.adapter = object : RecyclerView.Adapter<ImageViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                val img = ImageView(parent.context)
                img.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                img.scaleType = ImageView.ScaleType.FIT_CENTER
                return ImageViewHolder(img)
            }

            override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                val url = images[position]
                holder.image.transitionName = "moment_image_${momentId}_$position"
                Glide.with(holder.image).load(url).into(holder.image)
            }

            override fun getItemCount(): Int = images.size
        }
        viewPager.setCurrentItem(index, false)
    }

    class ImageViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
}