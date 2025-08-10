package com.xly.business.user

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.user.adapter.BubbleAdapter
import com.xly.business.user.adapter.BubbleItem
import com.xly.databinding.ActivityBubblesBinding
import com.xly.index.LYMainActivity

class BubbleChooseActivity : LYBaseActivity<ActivityBubblesBinding, com.xly.business.login.viewmodel.LoginViewModel>() {

    private lateinit var adapter: BubbleAdapter
    private val bubbleItems = mutableListOf<BubbleItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupRecyclerView()
        setupConfirmButton()
        updateButtonState()
    }

    private fun setupRecyclerView() {
        val recycler = viewBind.bubbleRecycler

        // Flexbox：主轴水平（横向滚动），并允许 wrap 到多行（产生多行，横向滚动）
        val flex = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            // justifyContent, alignItems 可按需微调
        }
        recycler.layoutManager = flex

        // 生成示例数据
        for (i in 1..50) {
            bubbleItems.add(BubbleItem("标签$i", selected = false))
        }
        adapter = BubbleAdapter(this, bubbleItems) { selectedCount ->
            // 当选择状态改变时更新按钮状态
            updateButtonState()
        }
        recycler.adapter = adapter

        // 让RecyclerView本身可以横向滑动（flex 主轴为行时会横向滚动）
        recycler.isHorizontalScrollBarEnabled = false
        recycler.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setupConfirmButton() {
        // 默认按钮不可点击
        viewBind.confirmBtn.isEnabled = false
        
        viewBind.confirmBtn.setOnClickListener {
            if (getSelectedCount() >= 8) {
                // 跳转到主页面
                val intent = Intent(this, LYMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun updateButtonState() {
        val selectedCount = getSelectedCount()
        val confirmBtn = viewBind.confirmBtn
        
        if (selectedCount >= 8) {
            // 按钮高亮可点击
            confirmBtn.isEnabled = true
            confirmBtn.text = "选好了"
            confirmBtn.setTextColor(Color.WHITE)
            confirmBtn.setBackgroundResource(R.drawable.flamingo_radius_btn)
        } else {
            // 按钮不可点击
            confirmBtn.isEnabled = false
            confirmBtn.text = "至少选 $selectedCount/8 个"
            confirmBtn.setTextColor(Color.parseColor("#77FFFFFF"))
            confirmBtn.setBackgroundResource(R.drawable.outline_pill)
        }
    }

    private fun getSelectedCount(): Int {
        return bubbleItems.count { it.selected }
    }

    override fun inflateBinding(layoutInflater: android.view.LayoutInflater) = ActivityBubblesBinding.inflate(layoutInflater)
    override fun initViewModel() = androidx.lifecycle.ViewModelProvider(this)[com.xly.business.login.viewmodel.LoginViewModel::class.java]
}