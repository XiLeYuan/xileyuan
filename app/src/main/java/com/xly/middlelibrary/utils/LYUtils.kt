package com.xly.middlelibrary.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

object LYUtils {

    fun getRandomColor(): Int {
        val rnd = java.util.Random()
        val red = rnd.nextInt(256)
        val green = rnd.nextInt(256)
        val blue = rnd.nextInt(256)
        return android.graphics.Color.rgb(red, green, blue)
    }
    fun createColorBitmap(color: Int, width: Int, height: Int): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(color)
        return bmp
    }

    fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float = 20f): Bitmap {
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }

    /**
     * 检查存储权限
     */
    fun checkStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 使用更细粒度的权限
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 使用 MANAGE_EXTERNAL_STORAGE
            true // 对于图片选择，通常不需要特殊权限
        } else {
            // Android 10及以下
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 申请存储权限
     */
    fun requestStoragePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 申请 READ_MEDIA_IMAGES 权限
            XXPermissions.with(activity)
                .permission(Permission.READ_MEDIA_IMAGES)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        // 权限申请成功
                    }
                    
                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        // 权限申请失败
                    }
                })
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 通常不需要特殊权限申请
        } else {
            // Android 10及以下申请存储权限
            XXPermissions.with(activity)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        // 权限申请成功
                    }
                    
                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        // 权限申请失败
                    }
                })
        }
    }
}