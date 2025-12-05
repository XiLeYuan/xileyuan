package com.xly.business.login.view

import android.content.Context
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xly.R
import com.xly.databinding.DialogHeightPickerBinding

class HeightPickerDialog(
    context: Context,
    private val options: List<String>,
    private val initialValue: String?,
    private val onItemSelected: (String) -> Unit
) : BottomSheetDialog(context) {
    
    private var binding: DialogHeightPickerBinding = DialogHeightPickerBinding.inflate(LayoutInflater.from(context))
    private var selectedPosition = -1
    private var layoutManager: LinearLayoutManager? = null
    private val itemHeight = (56 * context.resources.displayMetrics.density).toInt() // 56dp转px
    private val visibleItemCount = 5 // 显示5个item，中间为选中项
    private var isInitialScroll = true // 标记是否为初始滚动

    init {
        setContentView(binding.root)
        setupViews()
    }
    
    override fun onStart() {
        super.onStart()
        // 设置底部弹窗的圆角
        val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.background = null
            it.post {
                val cornerRadius = 24 * context.resources.displayMetrics.density
                it.clipToOutline = true
                it.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        if (view.width > 0 && view.height > 0) {
                            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                        }
                    }
                }
                // 在弹窗显示后立即定位，此时RecyclerView应该已经测量完成
                binding.rvOptions.post {
                    val layoutManager = binding.rvOptions.layoutManager as? LinearLayoutManager ?: return@post
                    if (binding.rvOptions.height > 0 && isInitialScroll) {
                        val centerY = binding.rvOptions.height / 2
                        val offset = centerY - itemHeight / 2
                        layoutManager.scrollToPositionWithOffset(selectedPosition, offset)
                        (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
                        updateSelectedItem()
                        isInitialScroll = false
                    }
                }
            }
        }
        // 设置窗口背景为透明
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setupViews() {
        // 设置初始选中位置，默认定位到170cm
        initialValue?.let {
            selectedPosition = options.indexOf(it)
            if (selectedPosition < 0) {
                // 如果没有找到，默认定位到170cm
                selectedPosition = options.indexOf("170cm")
                if (selectedPosition < 0) {
                    selectedPosition = options.size / 2 // 如果170cm不存在，选中中间
                }
            }
        } ?: run {
            // 默认定位到170cm
            selectedPosition = options.indexOf("170cm")
            if (selectedPosition < 0) {
                selectedPosition = options.size / 2 // 如果170cm不存在，选中中间
            }
        }

        // 设置关闭按钮
        binding.ivCancel.setOnClickListener {
            dismiss()
        }

        // 设置确定按钮
        binding.tvConfirm.setOnClickListener {
            // 获取当前中间选中的位置
            val centerPosition = findCenterPosition()
            if (centerPosition >= 0 && centerPosition < options.size) {
                selectedPosition = centerPosition
                onItemSelected(options[selectedPosition])
                dismiss()
            }
        }

        // 设置RecyclerView
        layoutManager = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.rvOptions.layoutManager = layoutManager
        binding.rvOptions.adapter = HeightPickerAdapter(options, selectedPosition) { position ->
            // 点击item时，更新选中位置但不关闭，需要点击确定才关闭
            selectedPosition = position
            snapToPosition(position)
        }
        
        // 添加滚动监听，实现中间选中效果
        // 修改滚动监听，初始滚动时不触发snapToCenter
        binding.rvOptions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isInitialScroll) {
                    updateSelectedItem()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isInitialScroll) {
                    // 滚动停止时，自动对齐到最近的item（但不在初始滚动时触发）
                    snapToCenter()
                }
            }
        })

        // 初始定位到选中位置（170cm），使用不带动画的直接定位
        // 在弹窗显示前就设置好位置，避免可见的滚动动画
        binding.rvOptions.post {
            val layoutManager = binding.rvOptions.layoutManager as? LinearLayoutManager ?: return@post
            // 确保RecyclerView已经测量完成
            if (binding.rvOptions.height > 0) {
                val centerY = binding.rvOptions.height / 2
                val offset = centerY - itemHeight / 2
                // 直接定位，不使用动画
                layoutManager.scrollToPositionWithOffset(selectedPosition, offset)
                // 更新适配器的选中位置
                (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
                // 立即更新选中状态
                updateSelectedItem()
                // 标记初始滚动完成
                isInitialScroll = false
            } else {
                // 如果高度还没测量完成，等待一下再定位
                binding.rvOptions.postDelayed({
                    val centerY = binding.rvOptions.height / 2
                    val offset = centerY - itemHeight / 2
                    layoutManager.scrollToPositionWithOffset(selectedPosition, offset)
                    (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
                    updateSelectedItem()
                    // 标记初始滚动完成
                    isInitialScroll = false
                }, 50) // 减少延迟时间
            }
        }
    }

    private fun updateSelectedItem() {
        val centerPosition = findCenterPosition()
        if (centerPosition >= 0 && centerPosition < options.size && centerPosition != selectedPosition) {
            val oldPosition = selectedPosition
            selectedPosition = centerPosition
            (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
            // 通知RecyclerView更新
            binding.rvOptions.adapter?.notifyItemChanged(oldPosition)
            binding.rvOptions.adapter?.notifyItemChanged(selectedPosition)
        }
    }

    private fun findCenterPosition(): Int {
        val layoutManager = binding.rvOptions.layoutManager as? LinearLayoutManager ?: return -1
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        
        // 计算中间位置
        val centerY = binding.rvOptions.height / 2
        var closestPosition = -1
        var minDistance = Int.MAX_VALUE

        for (i in firstVisible..lastVisible) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            val viewCenterY = view.top + view.height / 2
            val distance = kotlin.math.abs(centerY - viewCenterY)
            if (distance < minDistance) {
                minDistance = distance
                closestPosition = i
            }
        }

        return closestPosition
    }

    private fun snapToCenter() {
        val centerPosition = findCenterPosition()
        if (centerPosition >= 0 && centerPosition < options.size) {
            scrollToPosition(centerPosition)
            // 更新选中位置
            val oldPosition = selectedPosition
            selectedPosition = centerPosition
            (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
            if (oldPosition != selectedPosition) {
                binding.rvOptions.adapter?.notifyItemChanged(oldPosition)
                binding.rvOptions.adapter?.notifyItemChanged(selectedPosition)
            }
        }
    }

    private fun snapToPosition(position: Int) {
        if (position >= 0 && position < options.size) {
            scrollToPosition(position)
            // 更新选中位置
            val oldPosition = selectedPosition
            selectedPosition = position
            (binding.rvOptions.adapter as? HeightPickerAdapter)?.updateSelectedPosition(selectedPosition)
            if (oldPosition != selectedPosition) {
                binding.rvOptions.adapter?.notifyItemChanged(oldPosition)
                binding.rvOptions.adapter?.notifyItemChanged(selectedPosition)
            }
        }
    }

    private fun scrollToPosition(position: Int) {
        val layoutManager = binding.rvOptions.layoutManager as? LinearLayoutManager ?: return
        val centerY = binding.rvOptions.height / 2
        val targetView = layoutManager.findViewByPosition(position)
        
        if (targetView != null) {
            val targetY = targetView.top + targetView.height / 2 - centerY
            if (kotlin.math.abs(targetY) > 1) { // 只有偏移超过1px才滚动
                binding.rvOptions.smoothScrollBy(0, targetY)
            }
        } else {
            // 如果view还未创建，先滚动到该位置
            // 计算offset，使item居中
            val offset = centerY - itemHeight / 2
            layoutManager.scrollToPositionWithOffset(position, offset)
        }
    }

    private class HeightPickerAdapter(
        private val options: List<String>,
        private var selectedPosition: Int,
        private val onItemSelected: (Int) -> Unit
    ) : RecyclerView.Adapter<HeightPickerAdapter.ViewHolder>() {

        fun updateSelectedPosition(position: Int) {
            val oldPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_height_picker_option, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val isSelected = position == selectedPosition
            holder.bind(options[position], isSelected)
            holder.itemView.setOnClickListener {
                updateSelectedPosition(position)
                onItemSelected(position)
            }
        }

        override fun getItemCount() = options.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.tvOption)

            fun bind(option: String, isSelected: Boolean) {
                textView.text = option
                if (isSelected) {
                    // 选中状态：字体变大，颜色变深，显示背景
                    textView.textSize = 18f
                    textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary_dark))
                    textView.setBackgroundResource(R.drawable.bg_height_picker_selected)
                } else {
                    // 未选中状态：正常字体，颜色较浅，无背景
                    textView.textSize = 16f
                    textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
                    textView.background = null
                }
            }
        }
    }
}

