package com.xly.business.login.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.FragmentUserInfoStepBinding
import com.xly.databinding.FragmentUserInfoStepGenderBinding
import com.xly.databinding.FragmentUserInfoStepAgeHeightBinding
import android.widget.SeekBar

class UserInfoStepFragment : LYBaseFragment<FragmentUserInfoStepBinding, LoginViewModel>() {
    private var stepIndex: Int = 0
    private var inputValidListener: OnInputValidListener? = null
    private var selectedGender: String? = null
    private var genderBinding: FragmentUserInfoStepGenderBinding? = null
    private var ageHeightBinding: FragmentUserInfoStepAgeHeightBinding? = null
    private var ageValue = 0
    private var heightValue = 0

    interface OnInputValidListener {
        fun onInputValid(step: Int, valid: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getInt(ARG_STEP_INDEX)?.let { stepIndex = it }
        inputValidListener = arguments?.getSerializable(ARG_LISTENER) as? OnInputValidListener
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUserInfoStepBinding {
        // 始终返回主表单binding，性别页不用viewBind
        return FragmentUserInfoStepBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return when (stepIndex) {
            0 -> {
                genderBinding = FragmentUserInfoStepGenderBinding.inflate(inflater, container, false)
                genderBinding!!.root
            }
            1 -> {
                ageHeightBinding = FragmentUserInfoStepAgeHeightBinding.inflate(inflater, container, false)
                ageHeightBinding!!.root
            }
            else -> {
                super.onCreateView(inflater, container, savedInstanceState)
            }
        }
    }

    override fun initViewModel(): LoginViewModel {
        return LoginViewModel() // 或 ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (stepIndex) {
            0 -> {
                genderBinding?.apply {
                    rlMale.setOnClickListener { selectGender(true) }
                    rlFemale.setOnClickListener { selectGender(false) }
                }
            }
            1 -> {
                ageHeightBinding?.apply {
                    // 年龄SeekBar
                    seekBarAge.max = 32 // 18~50+ 共33档
                    seekBarAge.progress = 0
                    tvAgeBubble.visibility = View.GONE
                    seekBarAge.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            val age = progress + 18
                            ageValue = if (progress == 32) 51 else age
                            val ageText = if (progress == 32) "50+岁" else "${age}岁"
                            tvAgeBubble.text = ageText
                            tvAgeBubble.visibility = View.VISIBLE
                            // 气泡位置
                            val bubbleWidth = tvAgeBubble.width.toFloat()
                            val seekBarWidth = seekBarAge.width.toFloat()
                            if (seekBarWidth > 0 && bubbleWidth > 0) {
                                val percent = progress.toFloat() / seekBarAge.max
                                val translationX = (seekBarWidth - bubbleWidth) * percent
                                tvAgeBubble.translationX = translationX
                            }
                            checkAgeHeightValid()
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            tvAgeBubble.visibility = View.VISIBLE
                        }
                        override fun onStopTrackingTouch(seekBar: SeekBar?) { }
                    })
                    // 身高SeekBar
                    seekBarHeight.max = 70 // 140~210 共71档
                    seekBarHeight.progress = 0
                    tvHeightBubble.visibility = View.GONE
                    seekBarHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            val height = progress + 140
                            heightValue = height
                            val heightText = "${height}cm"
                            tvHeightBubble.text = heightText
                            tvHeightBubble.visibility = View.VISIBLE
                            // 气泡位置
                            val bubbleWidth = tvHeightBubble.width.toFloat()
                            val seekBarWidth = seekBarHeight.width.toFloat()
                            if (seekBarWidth > 0 && bubbleWidth > 0) {
                                val percent = progress.toFloat() / seekBarHeight.max
                                val translationX = (seekBarWidth - bubbleWidth) * percent
                                tvHeightBubble.translationX = translationX
                            }
                            checkAgeHeightValid()
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            tvHeightBubble.visibility = View.VISIBLE
                        }
                        override fun onStopTrackingTouch(seekBar: SeekBar?) { }
                    })

                    // 初始化时计算一次气泡位置
                    tvAgeBubble.post {
                        val progress = seekBarAge.progress
                        val bubbleWidth = tvAgeBubble.width.toFloat()
                        val seekBarWidth = seekBarAge.width.toFloat()
                        if (seekBarWidth > 0 && bubbleWidth > 0) {
                            val percent = progress.toFloat() / seekBarAge.max
                            val translationX = (seekBarWidth - bubbleWidth) * percent
                            tvAgeBubble.translationX = translationX
                        }
                    }
                    tvHeightBubble.post {
                        val progress = seekBarHeight.progress
                        val bubbleWidth = tvHeightBubble.width.toFloat()
                        val seekBarWidth = seekBarHeight.width.toFloat()
                        if (seekBarWidth > 0 && bubbleWidth > 0) {
                            val percent = progress.toFloat() / seekBarHeight.max
                            val translationX = (seekBarWidth - bubbleWidth) * percent
                            tvHeightBubble.translationX = translationX
                        }
                    }
                }
            }
            else -> {
                // 示例：第0步为昵称输入
                viewBind.inputEdit.visibility = View.VISIBLE
                viewBind.inputEdit.hint = "步骤${stepIndex}内容"
                viewBind.inputEdit.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val valid = !s.isNullOrEmpty()
                        inputValidListener?.onInputValid(stepIndex, valid)
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
    }

    private fun selectGender(isMale: Boolean) {
        genderBinding?.apply {
            rlMale.setBackgroundResource(if (isMale) R.drawable.bg_gender_selected else R.drawable.bg_gender_unselected)
            rlFemale.setBackgroundResource(if (!isMale) R.drawable.bg_gender_selected else R.drawable.bg_gender_unselected)
            tvMale.setTextColor(if (isMale) resources.getColor(R.color.flamingo) else 0xFF888888.toInt())
            tvFemale.setTextColor(if (!isMale) resources.getColor(R.color.flamingo) else 0xFF888888.toInt())
            selectedGender = if (isMale) "male" else "female"
            inputValidListener?.onInputValid(0, true)
        }
    }

    private fun checkAgeHeightValid() {
        val valid = ageValue >= 18 && heightValue >= 140
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    companion object {
        private const val ARG_STEP_INDEX = "step_index"
        private const val ARG_LISTENER = "input_valid_listener"
        fun newInstance(step: Int, listener: OnInputValidListener): UserInfoStepFragment {
            val fragment = UserInfoStepFragment()
            val args = Bundle()
            args.putInt(ARG_STEP_INDEX, step)
            // 不能直接putSerializable接口，实际项目可用ViewModel或Activity持有Listener
            fragment.arguments = args
            fragment.inputValidListener = listener
            return fragment
        }
    }
} 