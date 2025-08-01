package com.jspp.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.xly.R
import com.jspp.model.UserCard
import com.jspp.widget.FloatingCardBehavior

class UserDetailActivity : AppCompatActivity() {

    private lateinit var floatingCardBehavior: FloatingCardBehavior<View>
    private lateinit var ivHeader: ImageView
    private lateinit var ivDetailAvatar: ImageView
    private lateinit var tvDetailName: android.widget.TextView
    private lateinit var tvDetailAge: android.widget.TextView
    private lateinit var tvDetailLocation: android.widget.TextView
    private lateinit var tvDetailBio: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail_floating)

        initViews()
        setupFloatingCard()
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

    private fun setupFloatingCard() {
        val floatingCard = findViewById<View>(R.id.floatingCard)
        
        // 获取Behavior实例
        val params = floatingCard.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
        floatingCardBehavior = params.behavior as FloatingCardBehavior<View>

        // 设置Behavior参数
        floatingCardBehavior.apply {
            setPeekHeight(220)
            
            // 设置滑动监听
            setOnSlideListener { slideOffset ->
                // 联动顶部图片
                ivHeader.translationY = -slideOffset * 200 // 图片上移
                ivHeader.alpha = 1f - 0.3f * slideOffset   // 图片透明度变化
            }

            // 设置状态变化监听
            setOnStateChangeListener { state ->
                when (state) {
                    FloatingCardBehavior.STATE_EXPANDED -> {
                        println("卡片完全展开")
                    }
                    FloatingCardBehavior.STATE_DRAGGING -> {
                        println("卡片拖动中")
                    }
                    FloatingCardBehavior.STATE_PEEK -> {
                        println("卡片露出")
                    }
                }
            }
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
        // 如果卡片是展开状态，先收起
        if (floatingCardBehavior.getCurrentSlideOffset() > 0.5f) {
            floatingCardBehavior.collapse()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 展开卡片
     */
    fun expandCard() {
        floatingCardBehavior.expand()
    }

    /**
     * 收起卡片
     */
    fun collapseCard() {
        floatingCardBehavior.collapse()
    }

    /**
     * 获取当前滑动偏移量
     */
    fun getCurrentSlideOffset(): Float {
        return floatingCardBehavior.getCurrentSlideOffset()
    }
}