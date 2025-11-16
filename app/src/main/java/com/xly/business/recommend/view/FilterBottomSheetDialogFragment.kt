package com.xly.business.recommend.view

import android.graphics.Outline
import android.graphics.Path
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xly.R
import com.xly.databinding.DialogFilterBottomSheetBinding

class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onConfirmClick: ((FilterOptions) -> Unit)? = null

    data class FilterOptions(
        val ageMin: Int = 18,
        val ageMax: Int = 50,
        val heightMin: Int = 150,
        val heightMax: Int = 200,
        val education: String = "不限",
        val income: String = "不限"
    )

    private var currentEducation: String = "不限"
    private var currentIncome: String = "不限"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
        
        // 在布局完成后设置圆角
        view.post {
            setupRoundedCorners()
        }
    }
    
    private fun setupRoundedCorners() {
        // 24dp 转换为像素
        val cornerRadius = 24 * resources.displayMetrics.density
        binding.root.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    if (view.width > 0 && view.height > 0) {
                        // 使用 Path 来只设置顶部两个角的圆角
                        val path = Path().apply {
                            moveTo(0f, cornerRadius)
                            quadTo(0f, 0f, cornerRadius, 0f)
                            lineTo(view.width - cornerRadius, 0f)
                            quadTo(view.width.toFloat(), 0f, view.width.toFloat(), cornerRadius)
                            lineTo(view.width.toFloat(), view.height.toFloat())
                            lineTo(0f, view.height.toFloat())
                            close()
                        }
                        outline.setConvexPath(path)
                    } else {
                        // 如果还没有测量完成，使用默认的圆角矩形
                        outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                    }
                }
            }
        }
    }

    private fun setupViews() {
        // 设置关闭按钮点击效果
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        // 设置学历标签点击
        setupEducationTags()
        
        // 设置收入标签点击
        setupIncomeTags()
    }

    private fun setupEducationTags() {
        val educationTags = listOf(
            binding.tvEducation1 to "不限",
            binding.tvEducation2 to "高中",
            binding.tvEducation3 to "大专",
            binding.tvEducation4 to "本科",
            binding.tvEducation5 to "硕士",
            binding.tvEducation6 to "博士"
        )

        educationTags.forEach { (view, value) ->
            view.setOnClickListener {
                selectEducationTag(view, educationTags.map { it.first })
                currentEducation = value
            }
        }
    }

    private fun setupIncomeTags() {
        val incomeTags = listOf(
            binding.tvIncome1 to "不限",
            binding.tvIncome2 to "5k以下",
            binding.tvIncome3 to "5k-10k",
            binding.tvIncome4 to "10k-20k",
            binding.tvIncome5 to "20k-50k",
            binding.tvIncome6 to "50k以上"
        )

        incomeTags.forEach { (view, value) ->
            view.setOnClickListener {
                selectIncomeTag(view, incomeTags.map { it.first })
                currentIncome = value
            }
        }
    }

    private fun selectEducationTag(selectedView: TextView, allViews: List<TextView>) {
        allViews.forEach { view ->
            if (view == selectedView) {
                view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_tag_selected)
                view.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            } else {
                view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_tag)
                view.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }
    }

    private fun selectIncomeTag(selectedView: TextView, allViews: List<TextView>) {
        allViews.forEach { view ->
            if (view == selectedView) {
                view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_tag_selected)
                view.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            } else {
                view.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_filter_tag)
                view.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirm.setOnClickListener {
            val options = FilterOptions(
                ageMin = binding.tvAgeMin.text.toString().toIntOrNull() ?: 18,
                ageMax = binding.tvAgeMax.text.toString().toIntOrNull() ?: 50,
                heightMin = binding.tvHeightMin.text.toString().toIntOrNull() ?: 150,
                heightMax = binding.tvHeightMax.text.toString().toIntOrNull() ?: 200,
                education = currentEducation,
                income = currentIncome
            )
            onConfirmClick?.invoke(options)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            // 彻底移除默认的白色背景，让我们的圆角背景显示出来
            it.background = null
            it.setBackgroundDrawable(null)
            
            // 在布局完成后设置圆角
            it.post {
                val cornerRadius = 24 * resources.displayMetrics.density
                it.clipToOutline = true
                it.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        if (view.width > 0 && view.height > 0) {
                            val path = Path().apply {
                                moveTo(0f, cornerRadius)
                                quadTo(0f, 0f, cornerRadius, 0f)
                                lineTo(view.width - cornerRadius, 0f)
                                quadTo(view.width.toFloat(), 0f, view.width.toFloat(), cornerRadius)
                                lineTo(view.width.toFloat(), view.height.toFloat())
                                lineTo(0f, view.height.toFloat())
                                close()
                            }
                            outline.setConvexPath(path)
                        } else {
                            outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
                        }
                    }
                }
            }
            
            // 确保可以裁剪圆角
            binding.root.clipToOutline = true
            
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = true
        }
        
        // 也设置 window 背景为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // 设置窗口动画（从底部出现）
        dialog?.window?.setWindowAnimations(R.style.BottomSheetAnimation)
    }
    
    override fun dismiss() {
        val dialog = dialog as? BottomSheetDialog
        // 设置消失动画（向下消失）
        dialog?.window?.setWindowAnimations(R.style.BottomSheetDismissAnimation)
        super.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FilterBottomSheetDialogFragment {
            return FilterBottomSheetDialogFragment()
        }
    }
}

