package com.xly.business.login.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.jaygoo.widget.RangeSeekBar
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
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
import com.xly.databinding.FragmentUserInfoStepNicknameAvatarBinding
import com.xly.databinding.FragmentUserInfoStepSchoolBinding
import com.xly.middlelibrary.utils.GlideEngine
import com.xly.middlelibrary.utils.LYUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import androidx.recyclerview.widget.GridLayoutManager
import com.xly.business.login.view.adapter.LifePhotoAdapter
import com.xly.databinding.FragmentUserInfoStepLifePhotosBinding
import com.xly.databinding.FragmentUserInfoStepIntroBinding
import com.xly.databinding.FragmentUserInfoStepIntro2Binding
import com.xly.databinding.FragmentUserInfoStepIntro3Binding
import com.xly.databinding.FragmentUserInfoStepTagsBinding

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

    private var nicknameAvatarBinding: FragmentUserInfoStepNicknameAvatarBinding? = null
    private var nickname: String? = null
    private var avatarPath: String? = null

    private var lifePhotosBinding: FragmentUserInfoStepLifePhotosBinding? = null
    private val lifePhotoList = mutableListOf<String>()
    private lateinit var lifePhotoAdapter: LifePhotoAdapter

    private var introBinding: FragmentUserInfoStepIntroBinding? = null
    private var introText: String? = null

    private var intro2Binding: FragmentUserInfoStepIntro2Binding? = null
    private var intro2Text: String? = null

    private var intro3Binding: FragmentUserInfoStepIntro3Binding? = null
    private var intro3Text: String? = null

    private var tagsBinding: FragmentUserInfoStepTagsBinding? = null
    private val selectedTags = mutableSetOf<String>()

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
            9 -> {
                nicknameAvatarBinding = FragmentUserInfoStepNicknameAvatarBinding.inflate(inflater, container, false)
                nicknameAvatarBinding!!.root
            }
            10 -> {
                lifePhotosBinding = FragmentUserInfoStepLifePhotosBinding.inflate(inflater, container, false)
                lifePhotosBinding!!.root
            }
            11 -> {
                introBinding = FragmentUserInfoStepIntroBinding.inflate(inflater, container, false)
                introBinding!!.root
            }
            12 -> {
                intro2Binding = FragmentUserInfoStepIntro2Binding.inflate(inflater, container, false)
                intro2Binding!!.root
            }
            13 -> {
                intro3Binding = FragmentUserInfoStepIntro3Binding.inflate(inflater, container, false)
                intro3Binding!!.root
            }
            14 -> {
                tagsBinding = FragmentUserInfoStepTagsBinding.inflate(inflater, container, false)
                tagsBinding!!.root
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
            9 -> {
                nicknameAvatarBinding?.apply {
                    // 昵称输入
                    etNickname.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            nickname = s?.toString()
                            btnClearNickname.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                            checkNicknameAvatarValid()
                        }
                        override fun afterTextChanged(s: Editable?) {}
                    })
                    
                    btnClearNickname.setOnClickListener { 
                        etNickname.text?.clear() 
                    }
                    
                    // 头像选择
                    avatarContainer.setOnClickListener {
                        showAvatarGuideDialog()
                    }
                }
            }
            10 -> {
                lifePhotosBinding?.apply {
                    rvLifePhotos.layoutManager = GridLayoutManager(requireContext(), 3)
                    lifePhotoAdapter = LifePhotoAdapter(lifePhotoList,
                        onAddClick = { selectLifePhotos() },
                        onDeleteClick = { pos ->
                            lifePhotoList.removeAt(pos)
                            lifePhotoAdapter.notifyDataSetChanged()
                            checkLifePhotosValid()
                        }
                    )
                    rvLifePhotos.adapter = lifePhotoAdapter
                                         checkLifePhotosValid()
                 }
             }
             11 -> {
                 introBinding?.apply {
                     // 根据性别设置不同的示例文本
                     val gender = viewModel.gender
                     val exampleText = "示例：我是典型的白羊座，性格热情喜欢认识新朋友，遇到喜欢的人我一定会对他很好很好的。我偶尔也有多愁善感的一面，尤其是看到小动物有关的事很容易被感动。"
                     tvExample.text = exampleText
                     
                     // 设置文本变化监听
                     etIntro.addTextChangedListener(object : TextWatcher {
                         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                             val currentLength = s?.length ?: 0
                             tvCharCount.text = "$currentLength/100"
                             introText = s?.toString()
                             checkIntroValid()
                         }
                         override fun afterTextChanged(s: Editable?) {}
                     })
                 }
             }
             12 -> {
                 intro2Binding?.apply {
                     // 设置文本变化监听
                     etIntro2.addTextChangedListener(object : TextWatcher {
                         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                         @SuppressLint("SetTextI18n")
                         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                             val currentLength = s?.length ?: 0
                             tvCharCount2.text = "$currentLength/100"
                             intro2Text = s?.toString()
                             checkIntro2Valid()
                         }
                         override fun afterTextChanged(s: Editable?) {}
                     })
                 }
             }
             13 -> {
                 intro3Binding?.apply {
                     // 设置文本变化监听
                     etIntro3.addTextChangedListener(object : TextWatcher {
                         override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                             val currentLength = s?.length ?: 0
                             tvCharCount3.text = "$currentLength/100"
                             intro3Text = s?.toString()
                             checkIntro3Valid()
                         }
                         override fun afterTextChanged(s: Editable?) {}
                     })
                 }
             }
             14 -> {
                 tagsBinding?.apply {
                     initTags()
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

    private fun showAvatarGuideDialog() {
        AvatarGuideDialog(requireContext()) {
            // 点击"知道了，去选照片"后的回调
            selectAvatarFromGallery()
        }.show()
    }

    private fun selectAvatarFromGallery() {
        // 检查权限
        if (!LYUtils.checkStoragePermission(requireContext())) {
            LYUtils.requestStoragePermission(requireActivity())
            return
        }

        // 使用PictureSelector选择图片
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.instance)
            .setMaxSelectNum(1)
            .setSelectionMode(SelectModeConfig.SINGLE)
//            .setCropEngine(ImageCropEngine.instance)
//            .setCompressEngine(getCompressFileEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        val localMedia = result[0]
                        val filePath = localMedia?.availablePath
                        if (!filePath.isNullOrEmpty()) {
                            processSelectedImage(File(filePath))
                        }
                    }
                }

                override fun onCancel() {
                    // 用户取消选择
                }
            })
    }

    private fun processSelectedImage(originalFile: File) {
        // 检查文件大小
        val fileSizeKB = originalFile.length() / 1024
        Log.d("AvatarUpload", "Original file size: ${fileSizeKB}KB")
        
        if (fileSizeKB > 200) {
            // 需要压缩
            compressImage(originalFile)
        } else {
            // 不需要压缩，直接上传
            uploadImage(originalFile)
        }
    }

    private fun compressImage(originalFile: File) {
        Luban.with(requireContext())
            .load(originalFile)
            .ignoreBy(200) // 忽略200KB以下的文件
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    // 开始压缩
                    showLoading("正在压缩图片...")
                }

                override fun onSuccess(compressFile: File?) {
                    hideLoading()
                    if (compressFile != null) {
                        val compressedSizeKB = compressFile.length() / 1024
                        Log.d("AvatarUpload", "Compressed file size: ${compressedSizeKB}KB")
                        uploadImage(compressFile)
                    } else {
                        Toast.makeText(requireContext(), "图片压缩失败", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onError(e: Throwable?) {
                    hideLoading()
                    Toast.makeText(requireContext(), "图片压缩失败: ${e?.message}", Toast.LENGTH_SHORT).show()
                }
            }).launch()
    }

    private fun uploadImage(file: File) {
        showLoading("正在上传头像...")
        
        viewModel.uploadAvatar(file,
            onSuccess = { avatarUrl ->
                hideLoading()
                // 上传成功，更新UI
                nicknameAvatarBinding?.apply {
                    // 加载网络图片到ImageView
                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.bg_avatar_circle)
                        .error(R.drawable.bg_avatar_circle)
                        .into(ivAvatar)
                    
                    ivAvatar.visibility = View.VISIBLE
                    ivAddAvatar.visibility = View.GONE
                }
                
                avatarPath = avatarUrl
                viewModel.avatarUrl = avatarUrl
                checkNicknameAvatarValid()
                
                Toast.makeText(requireContext(), "头像上传成功", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMsg ->
                hideLoading()
                nicknameAvatarBinding?.apply {
                    // 加载网络图片到ImageView
                    Glide.with(requireContext())
                        .load(R.mipmap.head_img)
                        .placeholder(R.drawable.bg_avatar_circle)
                        .error(R.drawable.bg_avatar_circle)
                        .into(ivAvatar)

                    ivAvatar.visibility = View.VISIBLE
                    ivAddAvatar.visibility = View.GONE
                }

                avatarPath = "head_url"
                viewModel.avatarUrl = "head_url"
                checkNicknameAvatarValid()
                Toast.makeText(requireContext(), "头像上传失败: $errorMsg", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showLoading(message: String) {
        // 显示加载提示，可以使用ProgressDialog或自定义LoadingView
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun hideLoading() {
        // 隐藏加载提示
    }

    private fun checkNicknameAvatarValid() {
        val valid = !nickname.isNullOrEmpty() && !avatarPath.isNullOrEmpty()
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun selectLifePhotos() {
        if (!LYUtils.checkStoragePermission(requireContext())) {
            LYUtils.requestStoragePermission(requireActivity())
            return
        }
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.instance)
            .setMaxSelectNum(5 - lifePhotoList.size)
            .setSelectionMode(SelectModeConfig.MULTIPLE)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null && result.isNotEmpty()) {
                        for (media in result) {
                            val filePath = media?.availablePath
                            if (!filePath.isNullOrEmpty()) {
                                processLifePhoto(File(filePath))
                            }
                        }
                    }
                }
                override fun onCancel() {}
            })
    }
    private fun processLifePhoto(originalFile: File) {
        val fileSizeKB = originalFile.length() / 1024
        if (fileSizeKB > 200) {
            Luban.with(requireContext())
                .load(originalFile)
                .ignoreBy(200)
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() { showLoading("正在压缩图片...") }
                    override fun onSuccess(compressFile: File?) {
                        hideLoading()
                        if (compressFile != null) uploadLifePhoto(compressFile)
                    }
                    override fun onError(e: Throwable?) {
                        hideLoading()
                        Toast.makeText(requireContext(), "图片压缩失败: ${e?.message}", Toast.LENGTH_SHORT).show()
                    }
                }).launch()
        } else {
            uploadLifePhoto(originalFile)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun uploadLifePhoto(file: File) {
        showLoading("正在上传图片...")
        viewModel.uploadAvatar(file, // 复用头像上传接口，实际应有单独生活照接口
            onSuccess = { url ->
                hideLoading()
                lifePhotoList.add(url)
                lifePhotoAdapter.notifyDataSetChanged()
                checkLifePhotosValid()
            },
            onError = { msg ->
                hideLoading()
                Toast.makeText(requireContext(), "图片上传失败: $msg", Toast.LENGTH_SHORT).show()
                //TODO: DELETE BELOW
                lifePhotoList.add("http://gips0.baidu.com/it/u=3602773692,1512483864&fm=3028&app=3028&f=JPEG&fmt=auto?w=960&h=1280")
                lifePhotoList.add("http://gips3.baidu.com/it/u=100751361,1567855012&fm=3028&app=3028&f=JPEG&fmt=auto?w=960&h=1280")
                lifePhotoList.add("https://gips3.baidu.com/it/u=3732737575,1337431568&fm=3028&app=3028&f=JPEG&fmt=auto&q=100&size=f1440_2560")
                lifePhotoList.add("http://gips2.baidu.com/it/u=3944689179,983354166&fm=3028&app=3028&f=JPEG&fmt=auto?w=1024&h=1024")
                lifePhotoAdapter.notifyDataSetChanged()
            }
        )
    }
    private fun checkLifePhotosValid() {
        val valid = lifePhotoList.size >= 3
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkIntroValid() {
        val valid = !introText.isNullOrEmpty() && (introText?.length ?: 0) >= 10
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkIntro2Valid() {
        val valid = !intro2Text.isNullOrEmpty() && (intro2Text?.length ?: 0) >= 10
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun checkIntro3Valid() {
        val valid = !intro3Text.isNullOrEmpty() && (intro3Text?.length ?: 0) >= 10
        inputValidListener?.onInputValid(stepIndex, valid)
    }

    private fun initTags() {
        val tags = listOf(
            "有坚持很久的爱好", "爱运动", "有幽默感", "阳光开朗", "性格随和",
            "享受家庭时光", "对职业有规划", "成熟理智", "每天都很开心", "感性浪漫",
            "有自己的主见和想法", "比较细心", "身材匀称", "有颜值", "衣品好",
            "真诚重感情", "逻辑思维强", "有追求能一起进步", "想象力丰富", "善良有爱心",
            "生活习惯好", "想过安稳生活", "有一定经济能力", "对生活有追求", "有良好学历背景",
            "会享受生活", "有共同爱好", "善于沟通，会换位思考", "相比物质更看重爱情", "聊得来", "加班少"
        )

        tagsBinding?.flexboxLayout?.apply {
            removeAllViews()
            tags.forEach { tag ->
                val tagView = createTagView(tag)
                addView(tagView)
            }
        }
    }

    private fun createTagView(tag: String): TextView {
        return TextView(requireContext()).apply {
            text = tag
            setPadding(16, 8, 16, 8)
            setTextColor(resources.getColor(R.color.gray))
            background = resources.getDrawable(R.drawable.bg_tag_unselected)
            setOnClickListener {
                toggleTag(tag, this)
            }
            
            val params = com.google.android.flexbox.FlexboxLayout.LayoutParams(
                com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT,
                com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 8, 8, 8)
            layoutParams = params
        }
    }

    private fun toggleTag(tag: String, tagView: TextView) {
        if (selectedTags.contains(tag)) {
            // 取消选中
            selectedTags.remove(tag)
            tagView.setTextColor(resources.getColor(R.color.gray))
            tagView.background = resources.getDrawable(R.drawable.bg_tag_unselected)
        } else {
            // 选中
            if (selectedTags.size < 3) {
                selectedTags.add(tag)
                tagView.setTextColor(resources.getColor(R.color.flamingo))
                tagView.background = resources.getDrawable(R.drawable.bg_tag_selected)
            } else {
                Toast.makeText(requireContext(), "最多只能选择3个标签", Toast.LENGTH_SHORT).show()
            }
        }
        checkTagsValid()
    }

    private fun checkTagsValid() {
        val valid = selectedTags.isNotEmpty()
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