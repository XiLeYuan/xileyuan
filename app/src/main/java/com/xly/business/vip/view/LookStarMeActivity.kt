package com.xly.business.vip.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.base.LYBaseActivity
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.ActivityStarMeBinding
import com.xly.index.viewmodel.MainViewModel
import com.xly.middlelibrary.utils.click

class LookStarMeActivity: LYBaseActivity<ActivityStarMeBinding, MainViewModel>() {


    companion object {
        fun start(c: Context) {
            val intent = Intent(c,LookStarMeActivity::class.java)
            (c as? Activity)?.startActivity(intent)
        }
    }


    override fun initView() {

        viewBind.rechargeContainer.vipRechargeRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val options = listOf(
            VipRechargeOption("¥30", "30天VIP"),
            VipRechargeOption("¥88", "90天VIP"),
            VipRechargeOption("¥168", "180天VIP"),
            VipRechargeOption("¥299", "365天VIP")
        )

        viewBind.rechargeContainer.vipRechargeRecyclerView.adapter = VipRechargeAdapter(options) { selectedOption ->
            // 处理点击事件，如弹窗确认、跳转支付等
            showToast("选择了：${selectedOption.desc}")
        }
    }



    override fun initOnClick() {
        viewBind.lookStarBtn.click {
            showToast("查看喜欢我的人")
        }
        viewBind.cancelTv.click {
            finish()
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityStarMeBinding {
        return ActivityStarMeBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

}