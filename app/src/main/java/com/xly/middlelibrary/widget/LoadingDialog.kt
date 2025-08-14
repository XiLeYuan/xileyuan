package com.xly.middlelibrary.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.xly.R

class LoadingDialog private constructor(context: Context) : Dialog(context, R.style.LoadingDialogTheme) {

    private var progressBar: CircularProgressIndicator? = null
    private var tvHint: TextView? = null
    private var isShowing = false

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupWindow()
        setupViews()
        setupDialogProperties()
    }

    private fun setupWindow() {
        window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(android.view.Gravity.CENTER)
        }
    }

    private fun setupViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
        setContentView(view)

        progressBar = view.findViewById(R.id.progressBar)
        tvHint = view.findViewById(R.id.tvHint)
    }

    private fun setupDialogProperties() {
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }

    /**
     * 设置提示文字
     */
    fun setHintText(text: String) {
        tvHint?.text = text
    }

    /**
     * 显示加载对话框
     */
    override fun show() {
        if (!isShowing) {
            super.show()
            isShowing = true
            startProgressAnimation()
        }
    }

    /**
     * 隐藏加载对话框
     */
    override fun dismiss() {
        if (isShowing) {
            stopProgressAnimation()
            super.dismiss()
            isShowing = false
        }
    }

    private fun startProgressAnimation() {
        progressBar?.apply {
            isIndeterminate = true
            // 如果需要自定义动画，可以在这里添加
        }
    }

    private fun stopProgressAnimation() {
        progressBar?.apply {
            isIndeterminate = false
        }
    }

    companion object {
        @JvmStatic
        fun showLoading(context: Context, hintText: String = "加载中..."): LoadingDialog {
            return LoadingDialog(context).apply {
                setHintText(hintText)
                show()
            }
        }

        @JvmStatic
        fun create(context: Context, hintText: String = "加载中..."): LoadingDialog {
            return LoadingDialog(context).apply {
                setHintText(hintText)
            }
        }
    }
}