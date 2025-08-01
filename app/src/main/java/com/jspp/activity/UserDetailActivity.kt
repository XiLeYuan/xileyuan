package com.jspp.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xly.R
import com.jspp.model.UserCard

class UserDetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var ivHeader: ImageView
    private lateinit var ivDetailAvatar: ImageView
    private lateinit var tvDetailName: android.widget.TextView
    private lateinit var tvDetailAge: android.widget.TextView
    private lateinit var tvDetailLocation: android.widget.TextView
    private lateinit var tvDetailBio: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        // 设置初始状态
        bottomSheetBehavior.apply {
            peekHeight = 220  // 初始露出高度
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED

            // 添加滑动监听
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // 状态变化处理
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 滑动过程中处理
                    // slideOffset: -1（隐藏）~0（初始）~1（完全展开）

                    // 联动顶部图片
                    ivHeader.translationY = -slideOffset * 200 // 图片上移
                    ivHeader.alpha = 1f - 0.3f * slideOffset   // 图片透明度变化
                }
            })
        }
    }

    private fun loadUserData() {
        val userCard = intent.getParcelableExtra<UserCard>("user_card")
        userCard?.let { user ->
            // 加载头像
            Glide.with(this)
                .load(user.avatarUrl)
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
}