package com.xly.middlelibrary.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import java.io.File


class ImageCropEngine : CropEngine {
    override fun onStartCrop(
        fragment: Fragment, currentLocalMedia: LocalMedia,
        dataSource: ArrayList<LocalMedia>, requestCode: Int
    ) {
        /*val currentCropPath = currentLocalMedia.getAvailablePath()
        val inputUri: Uri?
        if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
            inputUri = Uri.parse(currentCropPath)
        } else {
            inputUri = Uri.fromFile(File(currentCropPath))
        }
        val fileName: String = DateUtils.getCreateFileName("CROP_") + ".jpg"
        val destinationUri = Uri.fromFile(File(getSandboxPath(), fileName))
        val options: UCrop.Options? = buildOptions()
        val dataCropSource = ArrayList<String?>()
        for (i in dataSource.indices) {
            val media = dataSource.get(i)
            dataCropSource.add(media.getAvailablePath())
        }
        val uCrop: UCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
        //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine() {
            public override fun loadImage(context: Context, url: String?, imageView: ImageView) {
                if (!ImageLoaderUtils.assertValidRequest(context)) {
                    return
                }
                Glide.with(context).load(url).override(180, 180).into(imageView)
            }

            public override fun loadImage(
                context: Context?,
                url: Uri?,
                maxWidth: Int,
                maxHeight: Int,
                call: OnCallbackListener<Bitmap?>?
            ) {
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)*/
    }
}