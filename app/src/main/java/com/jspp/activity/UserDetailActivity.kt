package com.jspp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.xly.R
import com.jspp.model.UserCard
import com.xly.base.LYBaseActivity
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.ActivityUserDetailFloatingBinding
import com.xly.middlelibrary.utils.click


class UserDetailActivity : LYBaseActivity<ActivityUserDetailFloatingBinding, LoginViewModel>() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        loadUserData()
        setupTransitions()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityUserDetailFloatingBinding {
        return ActivityUserDetailFloatingBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): LoginViewModel {
        return ViewModelProvider(this)[LoginViewModel::class.java]
    }

    private fun initViews() {



        viewBind.ivBack.click {
            finish()
        }



        // 设置共享元素转场
        viewBind.ivHeader.transitionName = "user_card"
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
                .into(viewBind.ivDetailAvatar)
            
            // 加载背景图片（这里使用头像作为背景）
            Glide.with(this)
                .load(user.avatarUrl)
                .placeholder(R.mipmap.find_img_3)
                .centerCrop()
                .into(viewBind.ivHeader)
            
            // 设置用户信息
            viewBind.tvDetailName.text = user.name
            viewBind.tvDetailAge.text = "${user.age}岁"
            viewBind.tvDetailLocation.text = user.location
            viewBind.tvDetailBio.text = user.bio
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
        finish()

    }




}