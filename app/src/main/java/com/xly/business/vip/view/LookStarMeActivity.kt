package com.xly.business.vip.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.databinding.ActivityStarMeBinding
import com.xly.index.viewmodel.MainViewModel

class LookStarMeActivity: LYBaseActivity<ActivityStarMeBinding, MainViewModel>() {


    companion object {
        fun start(c: Context) {
            val intent = Intent(c,LookStarMeActivity::class.java)
            (c as? Activity)?.startActivity(intent)
            (c as? Activity)?.finish()
        }
    }


    override fun initView() {

    }

    override fun initOnClick() {
        viewBind.lookStarBtn.setOnClickListener {
            showToast("查看喜欢我的人")
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityStarMeBinding {
        return ActivityStarMeBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

}