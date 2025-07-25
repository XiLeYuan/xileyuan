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
import com.jaygoo.widget.RangeSeekBar
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.FragmentUserInfoStepAddressBinding
import com.xly.databinding.FragmentUserInfoStepAgeHeightBinding
import com.xly.databinding.FragmentUserInfoStepBinding
import com.xly.databinding.FragmentUserInfoStepEducationBinding
import com.xly.databinding.FragmentUserInfoStepGenderBinding
import com.xly.databinding.FragmentUserInfoStepHouseCarBinding
import com.xly.databinding.FragmentUserInfoStepJobIncomeBinding
import com.xly.databinding.FragmentUserInfoStepMarriageChildrenBinding
import com.xly.databinding.FragmentUserInfoStepMarryPlanBinding
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

    private var houseCarBinding: FragmentUserInfoStepHouseCarBinding? = null
    private var selectedHouse: String? = null
    private var selectedCar: String? = null

    private var marriageChildrenBinding: FragmentUserInfoStepMarriageChildrenBinding? = null
    private var selectedMarriage: String? = null
    private var selectedChildren: String? = null

    private var marryPlanBinding: FragmentUserInfoStepMarryPlanBinding? = null
    private var selectedPlan: String? = null
    private var ageRange: Pair<Int, Int>? = null

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
            6 -> {
                houseCarBinding = FragmentUserInfoStepHouseCarBinding.inflate(inflater, container, false)
                houseCarBinding!!.root
            }
            7 -> {
                marriageChildrenBinding = FragmentUserInfoStepMarriageChildrenBinding.inflate(inflater, container, false)
                marriageChildrenBinding!!.root
            }
            8 -> {
                marryPlanBinding = FragmentUserInfoStepMarryPlanBinding.inflate(inflater, container, false)
                marryPlanBinding!!.root
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
            6 -> {
                houseCarBinding?.apply {
                    val houseOptions = listOf(
                        tvHouse1 to "no_house",
                        tvHouse2 to "house_no_loan",
                        tvHouse3 to "house_with_loan",
                        tvHouse4 to "multi_house"
                    )
                    val carOptions = listOf(
                        tvCar1 to "no_car",
                        tvCar2 to "car_no_loan",
                        tvCar3 to "car_with_loan"
                    )
                    houseOptions.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectHouse(tv, value)
                            checkHouseCarValid()
                        }
                    }
                    carOptions.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectCar(tv, value)
                            checkHouseCarValid()
                        }
                    }
                }
            }
            7 -> {
                marriageChildrenBinding?.apply {
                    val marriageOptions = listOf(
                        tvMarriage1 to "single",
                        tvMarriage2 to "divorced",
                        tvMarriage3 to "widowed"
                    )
                    val childrenOptions = listOf(
                        tvChildren1 to "no_child",
                        tvChildren2 to "child_with_me",
                        tvChildren3 to "child_with_other"
                    )
                    marriageOptions.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectMarriage(tv, value)
                            checkMarriageChildrenValid()
                        }
                    }
                    childrenOptions.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectChildren(tv, value)
                            checkMarriageChildrenValid()
                        }
                    }
                }
            }
            8 -> {
                marryPlanBinding?.apply {
                    val planOptions = listOf(
                        tvPlan1 to "1year",
                        tvPlan2 to "2year",
                        tvPlan3 to "3year",
                        tvPlan4 to "anytime"
                    )
                    planOptions.forEach { (tv, value) ->
                        tv.setOnClickListener {
                            selectPlan(tv, value)
                            checkMarryPlanValid()
                        }
                    }
                    // 年龄范围选择（Jay-Goo RangeSeekBar）
                    rangeSeekBar.setOnRangeChangedListener(object : com.jaygoo.widget.OnRangeChangedListener {
                        override fun onRangeChanged(rangeSeekBar: RangeSeekBar?, min: Float, max: Float, isFromUser: Boolean) {
                            ageRange = min.toInt() to max.toInt()
                            tvAgeRange.text = if (max.toInt() >= 50) "${min.toInt()}岁-50+岁" else "${min.toInt()}岁-${max.toInt()}岁"
                            checkMarryPlanValid()
                        }

                        override fun onStartTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                            // 可选实现
                        }

                        override fun onStopTrackingTouch(view: RangeSeekBar, isLeft: Boolean) {
                            // 可选实现
                        }
                    })
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

    private fun selectHouse(tv: TextView, value: String) {
        houseCarBinding?.apply {
            val all = listOf(tvHouse1, tvHouse2, tvHouse3, tvHouse4)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedHouse = value
        }
    }
    private fun selectCar(tv: TextView, value: String) {
        houseCarBinding?.apply {
            val all = listOf(tvCar1, tvCar2, tvCar3)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedCar = value
        }
    }

    private fun selectMarriage(tv: TextView, value: String) {
        marriageChildrenBinding?.apply {
            val all = listOf(tvMarriage1, tvMarriage2, tvMarriage3)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedMarriage = value
        }
    }
    private fun selectChildren(tv: TextView, value: String) {
        marriageChildrenBinding?.apply {
            val all = listOf(tvChildren1, tvChildren2, tvChildren3)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedChildren = value
        }
    }

    private fun selectPlan(tv: TextView, value: String) {
        marryPlanBinding?.apply {
            val all = listOf(tvPlan1, tvPlan2, tvPlan3, tvPlan4)
            all.forEach {
                it.setBackgroundResource(if (it == tv) R.drawable.bg_education_selected else R.drawable.bg_education_unselected)
                it.setTextColor(if (it == tv) resources.getColor(R.color.flamingo) else 0xFF222222.toInt())
            }
            selectedPlan = value
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

    private fun checkHouseCarValid() {
        val valid = !selectedHouse.isNullOrEmpty() && !selectedCar.isNullOrEmpty()
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkMarriageChildrenValid() {
        val valid = !selectedMarriage.isNullOrEmpty() && !selectedChildren.isNullOrEmpty()
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkMarryPlanValid() {
        val valid = !selectedPlan.isNullOrEmpty() && ageRange != null && ageRange!!.first >= 18 && ageRange!!.second >= ageRange!!.first
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