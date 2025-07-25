package com.xly.business.login.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.FragmentUserInfoStepAddressBinding
import com.xly.databinding.FragmentUserInfoStepAgeHeightBinding
import com.xly.databinding.FragmentUserInfoStepBinding
import com.xly.databinding.FragmentUserInfoStepEducationBinding
import com.xly.databinding.FragmentUserInfoStepGenderBinding
import com.xly.databinding.FragmentUserInfoStepJobIncomeBinding
import com.xly.databinding.FragmentUserInfoStepSchoolBinding

class UserInfoStepFragment : LYBaseFragment<FragmentUserInfoStepBinding, LoginViewModel>() {
    private var stepIndex: Int = 0
    private var inputValidListener: OnInputValidListener? = null
    private var selectedGender: String? = null

    private var genderBinding: FragmentUserInfoStepGenderBinding? = null
    private var ageHeightBinding: FragmentUserInfoStepAgeHeightBinding? = null
    private var ageValue = 0
    private var heightValue = 0
    private var educationBinding: FragmentUserInfoStepEducationBinding? = null
    private var selectedEducation: String? = null

    private var schoolBinding: FragmentUserInfoStepSchoolBinding? = null
    private var jobIncomeBinding: FragmentUserInfoStepJobIncomeBinding? = null
    private var selectedIncome: String? = null

    private var addressBinding: FragmentUserInfoStepAddressBinding? = null
    private var currentAddress: String? = null
    private var hometownAddress: String? = null

    interface OnInputValidListener {
        fun onInputValid(step: Int, valid: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getInt(ARG_STEP_INDEX)?.let { stepIndex = it }
        inputValidListener = context as? OnInputValidListener
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
            2 -> {
                educationBinding = FragmentUserInfoStepEducationBinding.inflate(inflater, container, false)
                educationBinding!!.root
            }
            3 -> {
                schoolBinding = FragmentUserInfoStepSchoolBinding.inflate(inflater, container, false)
                schoolBinding!!.root
            }
            4 -> {
                jobIncomeBinding = FragmentUserInfoStepJobIncomeBinding.inflate(inflater, container, false)
                jobIncomeBinding!!.root
            }
            5 -> {
                addressBinding = FragmentUserInfoStepAddressBinding.inflate(inflater, container, false)
                addressBinding!!.root
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
                    // 恢复性别
                    val savedGender = viewModel.gender
                    if (savedGender != null) {
                        selectGender(savedGender == "male", fromRestore = true)
                    }
                    rlMale.setOnClickListener { selectGender(true) }
                    rlFemale.setOnClickListener { selectGender(false) }
                }
            }
            1 -> {
                ageHeightBinding?.apply {
                    // 恢复年龄
                    val savedAge = viewModel.age
                    if (savedAge != null) {
                        val progress = if (savedAge >= 51) 32 else (savedAge - 18)
                        seekBarAge.progress = progress
                    }
                    // 恢复身高
                    val savedHeight = viewModel.height
                    if (savedHeight != null) {
                        seekBarHeight.progress = savedHeight - 140
                    }
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
                            android.util.Log.d("UserInfoStep", "Age changed: $ageValue")
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
                            android.util.Log.d("UserInfoStep", "Height changed: $heightValue")
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
            2 -> {
                educationBinding?.apply {
                    val options = listOf(
                        tvDoctor to "doctor",
                        tvMaster to "master",
                        tvBachelor to "bachelor",
                        tvJuniorCollege to "junior_college",
                        tvBelowJunior to "below_junior"
                    )
                    val savedEdu = viewModel.education
                    if (savedEdu != null) {
                        options.find { it.second == savedEdu }?.let { selectEducation(it.first, it.second, fromRestore = true) }
                    }
                    options.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectEducation(tv, value)
                            // 移除自动跳转逻辑，跳转交由Activity控制
                        }
                    }
                }
            }
            3 -> {
                schoolBinding?.apply {
                    etSchool.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            btnClearSchool.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                            inputValidListener?.onInputValid(stepIndex, !s.isNullOrEmpty())
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                    btnClearSchool.setOnClickListener { etSchool.text?.clear() }
                }
            }
            4 -> {
                jobIncomeBinding?.apply {
                    etJob.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            btnClearJob.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                            checkJobIncomeValid()
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                    btnClearJob.setOnClickListener { etJob.text?.clear() }
                    val incomeOptions = listOf(
                        tvIncome1, tvIncome2, tvIncome3, tvIncome4, tvIncome5, tvIncome6, tvIncome7
                    )
                    incomeOptions.forEach { tv ->
                        tv.setOnClickListener {
                            selectIncome(tv)
                            checkJobIncomeValid()
                        }
                    }
                }
            }
            5 -> {
                addressBinding?.apply {
                    tvCurrentAddress.setOnClickListener {
                        val addressJson = AddressPickerDialog.loadAddressJson(requireContext())
                        AddressPickerDialog(requireContext(), addressJson) { p, c, d ->
                            currentAddress = "$p $c $d"
                            tvCurrentAddress.text = currentAddress
                            checkAddressValid()
                        }.show()
                    }
                    tvHometownAddress.setOnClickListener {
                        val addressJson = AddressPickerDialog.loadAddressJson(requireContext())
                        AddressPickerDialog(requireContext(), addressJson) { p, c, d ->
                            hometownAddress = "$p $c $d"
                            tvHometownAddress.text = hometownAddress
                            checkAddressValid()
                        }.show()
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

    private fun selectGender(isMale: Boolean, fromRestore: Boolean = false) {
        genderBinding?.apply {
            rlMale.setBackgroundResource(if (isMale) R.drawable.bg_gender_selected else R.drawable.bg_gender_unselected)
            rlFemale.setBackgroundResource(if (!isMale) R.drawable.bg_gender_selected else R.drawable.bg_gender_unselected)
            tvMale.setTextColor(if (isMale) resources.getColor(R.color.flamingo) else 0xFF888888.toInt())
            tvFemale.setTextColor(if (!isMale) resources.getColor(R.color.flamingo) else 0xFF888888.toInt())
            selectedGender = if (isMale) "male" else "female"
            viewModel.gender = selectedGender // 保存到ViewModel
            android.util.Log.d("UserInfoStep", "Gender selected: $selectedGender, step=$stepIndex")
            if (!fromRestore) {
                inputValidListener?.onInputValid(stepIndex, true)
            }
        }
    }

    private fun selectEducation(tv: TextView, value: String, fromRestore: Boolean = false) {
        educationBinding?.apply {
            val all = listOf(tvDoctor, tvMaster, tvBachelor, tvJuniorCollege, tvBelowJunior)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedEducation = value
            viewModel.education = value
            if (!fromRestore) {
                inputValidListener?.onInputValid(stepIndex, true)
            }
        }
    }

    private fun selectIncome(tv: TextView) {
        jobIncomeBinding?.apply {
            val all = listOf(tvIncome1, tvIncome2, tvIncome3, tvIncome4, tvIncome5, tvIncome6, tvIncome7)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedIncome = tv.text.toString()
        }
    }

    private fun checkAgeHeightValid() {
        val valid = ageValue >= 18 && heightValue >= 140
        // 保存到ViewModel
        if (ageValue >= 18) viewModel.age = ageValue
        if (heightValue >= 140) viewModel.height = heightValue
        android.util.Log.d("UserInfoStep", "Checking valid: age=$ageValue, height=$heightValue, valid=$valid, step=$stepIndex")
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkJobIncomeValid() {
        jobIncomeBinding?.apply {
            val jobValid = !etJob.text.isNullOrEmpty()
            val incomeValid = selectedIncome != null
            inputValidListener?.onInputValid(stepIndex, jobValid && incomeValid)
        }
    }

    private fun checkAddressValid() {
        val valid = !currentAddress.isNullOrEmpty() && !hometownAddress.isNullOrEmpty()
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    companion object {
        private const val ARG_STEP_INDEX = "step_index"
        fun newInstance(step: Int): UserInfoStepFragment {
            val fragment = UserInfoStepFragment()
            val args = Bundle()
            args.putInt(ARG_STEP_INDEX, step)
            fragment.arguments = args
            return fragment
        }
    }
} 