package com.jspp.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xly.R
import com.jspp.model.UserCard
import com.jspp.widget.AdvancedBottomSheetBehavior

class UserDetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: AdvancedBottomSheetBehavior<View>
    private lateinit var ivHeader: ImageView
    private lateinit var ivDetailAvatar: ImageView
    private lateinit var tvDetailName: android.widget.TextView
    private lateinit var tvDetailAge: android.widget.TextView
    private lateinit var tvDetailLocation: android.widget.TextView
    private lateinit var tvDetailBio: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail_custom)

        initViews()
        setupBottomSheet()
        loadUserData()
        setupTransitions()
    }

    private fun initViews() {
        ivHeader = findViewById(R.id.ivHeader)
        ivDetailAvatar = findViewById(R.id.ivDetailAvatar)
        tvDetailName = findViewById(R.id.tvDetailName)
        tvDetailAge = findViewById(R.id.tvDetailAge)
        tvDetailLocation = findViewById(R.id.tvDetailLocation)
        tvDetailBio = findViewById(R.id.tvDetailBio)

        // 设置返回按钮
        findViewById<View>(R.id.ivBack).setOnClickListener {
            onBackPressed()
        }

        // 设置共享元素转场
        ivHeader.transitionName = "user_card"
    }

    private fun setupBottomSheet() {
        val bottomSheetContainer = findViewById<View>(R.id.bottomSheetContainer)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer) as AdvancedBottomSheetBehavior<View>

        // 设置初始状态
        bottomSheetBehavior.apply {
            peekHeight = 220  // 初始露出高度
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED

            // 设置滑动监听
            setOnSlideListener { slideOffset ->
                // 联动顶部图片
                ivHeader.translationY = -slideOffset * 200 // 图片上移
                ivHeader.alpha = 1f - 0.3f * slideOffset   // 图片透明度变化
            }

            // 设置状态变化监听
            setOnStateChangeListener { state ->
                when (state) {
                    AdvancedBottomSheetBehavior.STATE_FULL -> {
                        // 完全展开状态
                        println("卡片完全展开")
                    }
                    AdvancedBottomSheetBehavior.STATE_HALF -> {
                        // 半展开状态
                        println("卡片半展开")
                    }
                    AdvancedBottomSheetBehavior.STATE_PEEK -> {
                        // 露出状态
                        println("卡片露出")
                    }
                }
            }

            // 添加状态变化监听
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // 状态变化处理
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 滑动过程中处理（这里由setOnSlideListener处理）
                }
            })
        }
    }

    private fun loadUserData() {
        // 使用getParcelableExtra的正确方式
        val userCard = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user_card", UserCard::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("user_card")
        }
        
        userCard?.let { user ->
            // 加载头像
            Glide.with(this)
                .load(user.avatarUrl)
                .placeholder(R.mipmap.head_img)
                .circleCrop()
                .into(ivDetailAvatar)
            
            // 加载背景图片（这里使用头像作为背景）
            Glide.with(this)
                .load(user.avatarUrl)
                .placeholder(R.mipmap.find_img_3)
                .centerCrop()
                .into(ivHeader)
            
            // 设置用户信息
            tvDetailName.text = user.name
            tvDetailAge.text = "${user.age}岁"
            tvDetailLocation.text = user.location
            tvDetailBio.text = user.bio
        }
    }

    private fun setupTransitions() {
        // 设置进入动画
        window.enterTransition?.duration = 300

        // 设置退出动画
        window.returnTransition?.duration = 300
    }

    override fun onBackPressed() {
        // 如果底部卡片是展开状态，先收起
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 展开底部卡片
     */
    fun expandBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * 收起底部卡片
     */
    fun collapseBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /**
     * 获取当前滑动偏移量
     */
    fun getCurrentSlideOffset(): Float {
        return bottomSheetBehavior.getCurrentSlideOffset()
    }
}