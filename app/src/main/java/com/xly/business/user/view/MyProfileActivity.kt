package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexboxLayout
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.square.view.adapter.BlurTransformation
import com.xly.business.user.UserInfo
import com.xly.business.user.view.adapter.ProfilePhotoAdapter
import com.xly.databinding.ActivityMyProfileBinding
import com.xly.middlelibrary.utils.click

class MyProfileActivity : LYBaseActivity<ActivityMyProfileBinding, MyProfileViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MyProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityMyProfileBinding {
        return ActivityMyProfileBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MyProfileViewModel {
        return ViewModelProvider(this)[MyProfileViewModel::class.java]
    }

    override fun initView() {
        setupBlurBackground()
        setupUserInfo()
        setupPhotos()
        setupBasicInfoTags()
    }

    override fun initOnClick() {
        // 返回按钮
        viewBind.ivBack.click {
            finish()
        }

        // 编辑基本资料按钮
        viewBind.ivEditBasicInfo.click {
            EditBasicInfoActivity.start(this)
        }
    }

    private fun setupBlurBackground() {
        // 使用默认头像作为模糊背景（实际项目中应该从用户资料获取）
        val avatarResourceId = resources.getIdentifier(
            "head_one",
            "mipmap",
            packageName
        )
        
        if (avatarResourceId != 0) {
            // 设置模糊背景
            Glide.with(this)
                .load(avatarResourceId)
                .transform(BlurTransformation(this, 25f))
                .into(viewBind.ivBlurBackground)
            
            // 设置头像（圆形裁剪）
            Glide.with(this)
                .load(avatarResourceId)
                .circleCrop()
                .into(viewBind.ivAvatar)
        } else {
            // 使用默认资源
            viewBind.ivBlurBackground.setImageResource(R.mipmap.head_img)
            viewBind.ivAvatar.setImageResource(R.mipmap.head_img)
        }
    }

    private fun setupUserInfo() {
        // TODO: 从 ViewModel 或 SharedPreferences 获取用户信息
        // 这里使用示例数据
        viewBind.tvBio.text = "热爱生活，喜欢旅行和摄影，希望找到志同道合的另一半"
        viewBind.tvMateRequirement.text = "希望对方年龄在25-35岁之间，身高170cm以上，有稳定工作，性格开朗，热爱生活"
    }

    private fun setupBasicInfoTags() {
        viewBind.llBasicInfoTags.removeAllViews()
        
        // 基本资料信息（示例数据）
        val basicInfo = listOf(
            "Ella",
            "28岁",
            "165cm",
            "天秤座",
            "清华大学/本科",
            "产品经理",
            "年薪20-30万",
            "已购车",
            "已购房",
            "家乡·河北",
            "现居地·北京"
        )
        
        val density = resources.displayMetrics.density
        
        basicInfo.forEach { info ->
            val tagView = TextView(this).apply {
                text = info
                textSize = 14f
                setTextColor(resources.getColor(R.color.text_primary_dark, null))
                background = resources.getDrawable(R.drawable.bg_profile_info_tag, null)
                setPadding(
                    (12 * density).toInt(),
                    (8 * density).toInt(),
                    (12 * density).toInt(),
                    (8 * density).toInt()
                )
                
                // 使用 FlexboxLayout.LayoutParams 支持自动换行
                val layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        0,
                        0,
                        (8 * density).toInt(),
                        (8 * density).toInt()
                    )
                }
                this.layoutParams = layoutParams
            }
            viewBind.llBasicInfoTags.addView(tagView)
        }
    }

    private fun setupPhotos() {
        // 初始化照片列表（示例数据）
        val photos = mutableListOf<Int>()
        // 添加示例图片资源
        val photoResources = listOf(
            resources.getIdentifier("head_one", "mipmap", packageName),
            resources.getIdentifier("head_two", "mipmap", packageName),
            resources.getIdentifier("head_three", "mipmap", packageName)
        ).filter { it != 0 }
        
        photos.addAll(photoResources)

        // 设置 RecyclerView
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewBind.rvPhotos.layoutManager = layoutManager

        val adapter = ProfilePhotoAdapter(
            photos = photos,
            maxPhotos = 6,
            onAddClick = {
                // 添加照片的点击事件
                showToast("添加照片")
                // TODO: 实现添加照片的逻辑
            },
            onPhotoClick = { position, photoResId ->
                // 照片点击事件
                showToast("查看照片 ${position + 1}")
                // TODO: 实现查看大图的逻辑
            }
        )
        viewBind.rvPhotos.adapter = adapter

        // 更新标题
        updatePhotosTitle(photos.size)
    }

    private fun updatePhotosTitle(currentCount: Int) {
        viewBind.tvPhotosTitle.text = "个人照片（$currentCount/6）"
    }
}

class MyProfileViewModel : ViewModel() {
    // 可以在这里添加获取用户资料的数据逻辑
}

