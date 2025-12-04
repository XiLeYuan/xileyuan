package com.xly.middlelibrary.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LifePhotoCropEngine : CropEngine {
    override fun onStartCrop(
        fragment: Fragment,
        currentLocalMedia: LocalMedia,
        dataSource: ArrayList<LocalMedia>,
        requestCode: Int
    ) {
        val currentCropPath = currentLocalMedia.availablePath
        val inputUri: Uri?
        
        // 处理输入 URI
        if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
            inputUri = Uri.parse(currentCropPath)
        } else {
            inputUri = Uri.fromFile(File(currentCropPath))
        }
        
        // 创建输出文件
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "CROP_LIFE_PHOTO_$timeStamp.jpg"
        val cacheDir = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: fragment.requireContext().cacheDir
        val outputFile = File(cacheDir, fileName)
        val destinationUri = Uri.fromFile(outputFile)
        
        // 配置 UCrop 选项
        val options = UCrop.Options().apply {
            // 设置裁剪框颜色
            setToolbarColor(fragment.requireContext().getColor(com.xly.R.color.bg_primary))
            setStatusBarColor(fragment.requireContext().getColor(com.xly.R.color.bg_primary))
            setActiveControlsWidgetColor(fragment.requireContext().getColor(com.xly.R.color.brand_primary))
            
            // 生活照使用自由裁剪（不固定比例）
            setFreeStyleCropEnabled(true)
            
            // 设置是否显示网格
            setShowCropGrid(true)
        }
        
        // 创建 UCrop 实例
        val uCrop = UCrop.of<Any>(inputUri, destinationUri)
            .withOptions(options)
        
        // 设置图片加载引擎
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                Glide.with(context)
                    .load(url)
                    .override(180, 180)
                    .into(imageView)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap?>?
            ) {
                Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .override(maxWidth, maxHeight)
                    .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                        ) {
                            call?.onCall(resource)
                        }

                        override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                            call?.onCall(null)
                        }
                    })
            }
        })
        
        // 启动裁剪 - 使用 Fragment 启动，PictureSelector 会自动处理结果
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }
}




