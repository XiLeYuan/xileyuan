package com.jspp.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xly.R
import com.jspp.widget.SimpleBottomSheetBehavior

class TestBottomSheetActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: SimpleBottomSheetBehavior<View>
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_bottom_sheet)

        initViews()
        setupBottomSheet()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        
        // 设置按钮点击事件
        findViewById<View>(R.id.btnExpand).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        
        findViewById<View>(R.id.btnCollapse).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = findViewById<View>(R.id.bottomSheetContainer)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer) as SimpleBottomSheetBehavior<View>

        bottomSheetBehavior.apply {
            peekHeight = 600
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED

            // 设置滑动监听
            setOnSlideListener { slideOffset ->
                tvStatus.text = "滑动偏移: ${String.format("%.2f", slideOffset)}"
            }

            // 添加状态变化监听
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    val stateText = when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> "完全展开"
                        BottomSheetBehavior.STATE_COLLAPSED -> "收起"
                        BottomSheetBehavior.STATE_HIDDEN -> "隐藏"
                        else -> "未知状态"
                    }
                    tvStatus.text = "状态: $stateText"
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 这里由setOnSlideListener处理
                }
            })
        }
    }
} 