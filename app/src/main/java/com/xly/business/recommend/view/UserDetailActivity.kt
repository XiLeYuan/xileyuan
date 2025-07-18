package com.xly.business.recommend.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.model.UserDetailItem
import com.xly.business.user.UserInfo
import com.xly.databinding.ActivityUserDetailBinding
import com.xly.index.viewmodel.MainViewModel
import com.xly.business.recommend.view.adapter.UserDetailAdapter

class UserDetailActivity : LYBaseActivity<ActivityUserDetailBinding,MainViewModel>() {




    companion object {

        fun start(c: Context) {
            val intent = Intent(c, UserDetailActivity::class.java)
            (c as? Activity)?.startActivity(intent)
            (c as? Activity)?.finish()
        }
    }


    override fun initView() {
//        val bottomSheet = findViewById<View>(R.id.bottomSheetContainer)
        val behavior = BottomSheetBehavior.from(viewBind.bottomSheetContainer)
        behavior.peekHeight = 220  // 卡片初始高度（可根据实际调整）
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // 可选：监听滑动，联动顶部图片缩放/透明度
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 你可以在这里动态调整顶部图片的透明度/缩放，实现联动动画
            }
        })
    }




    override fun onBackPressed() {
        // 支持返回动画
        super.onBackPressed()
    }


    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityUserDetailBinding {
        return ActivityUserDetailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

}