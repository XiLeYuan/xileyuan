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
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.model.UserDetailItem
import com.xly.business.user.UserInfo
import com.xly.databinding.ActivityUserDetailBinding
import com.xly.index.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xly.business.recommend.view.adapter.UserDetailAdapter

class UserDetailActivity : LYBaseActivity<ActivityUserDetailBinding,MainViewModel>() {


    val initialHeaderHeight = 300

    private var dragHandle: View ?= null
    private var handleIcon: View ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    companion object {

        fun start(c: Context) {
            val intent = Intent(c, UserDetailActivity::class.java)
            (c as? Activity)?.startActivity(intent)
            (c as? Activity)?.finish()
        }
    }


    override fun initView() {


        // 初始化底部面板
        val bottomSheetBehavior = BottomSheetBehavior.from(viewBind.bottomSheet)
        dragHandle = findViewById(R.id.drag_handle);
        handleIcon = findViewById(R.id.handle_icon);

        // 设置面板行为
        bottomSheetBehavior.setPeekHeight(120); // 初始高度

        // 自由停靠模式：允许面板停在任何位置
        bottomSheetBehavior.setFitToContents(false);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setHideable(false);
        dragHandle?.let {
            it.setOnClickListener {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        }


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    handleIcon?.setRotation(180f);
                } else {
                    handleIcon?.setRotation(0f);
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        // 在屏幕底部添加安全区域（用于内容滚动到底部时）
        val root = findViewById<View>(android.R.id.content)
        root.setPadding(0, 0, 0, getNavigationBarHeight());




// 设置滑动面板监听器


    }







    // 获取导航栏高度
    private fun getNavigationBarHeight(): Int {
//        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        val resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId)
        } else {
            return 0
        }
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