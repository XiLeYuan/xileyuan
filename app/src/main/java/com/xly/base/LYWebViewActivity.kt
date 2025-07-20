package com.xly.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.xly.databinding.ActivityWebviewBinding
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity
import android.widget.FrameLayout
import com.xly.R

class LYWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var webView: WebView
    private lateinit var titleView: TextView
    private lateinit var backBtn: ImageView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 动态添加标题栏
        val titleBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(48)
            )
            setBackgroundColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER_VERTICAL
            elevation = dp2px(2).toFloat()
        }
        backBtn = ImageView(this).apply {
            setImageResource(R.mipmap.ic_back)
            val size = dp2px(16)
            val params = LinearLayout.LayoutParams(size, size)
            params.marginStart = dp2px(12)
            layoutParams = params
            setOnClickListener {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        }
        titleView = TextView(this).apply {
            textSize = 18f
            setTextColor(0xFF222222.toInt())
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        titleBar.addView(backBtn)
        titleBar.addView(titleView)
        // 将标题栏加到根布局顶部
        (binding.root as FrameLayout).addView(titleBar, 0)

        val title = intent.getStringExtra("title") ?: ""
        val url = intent.getStringExtra("url") ?: ""
        titleView.text = title
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        // 设置WebView距离顶部margin，避免被标题栏遮挡
        val lp = binding.webView.layoutParams as FrameLayout.LayoutParams
        lp.topMargin = dp2px(48)
        binding.webView.layoutParams = lp
        binding.webView.loadUrl(url)
    }

    private fun dp2px(dp: Int): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
} 