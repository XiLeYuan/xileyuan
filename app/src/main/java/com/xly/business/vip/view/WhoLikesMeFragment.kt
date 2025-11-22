package com.xly.business.vip.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.base.LYBaseFragment
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.FragmentWhoLikesMeBinding
import com.xly.index.viewmodel.MainViewModel

class WhoLikesMeFragment : LYBaseFragment<FragmentWhoLikesMeBinding, MainViewModel>() {

    private var selectedOption: VipRechargeOption? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWhoLikesMeBinding {
        return FragmentWhoLikesMeBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun initView() {
        super.initView()
        val options = listOf(
            VipRechargeOption("连续包年", "¥696", "¥318", "26.5元/月", isRecommended = true, isContinuous = true),
            VipRechargeOption("连续包季", "¥174", "¥112", "37.3元/月", isContinuous = true),
            VipRechargeOption("连续包月", "¥58", "¥48", "48元/月", isContinuous = true),
            VipRechargeOption("12个月", "¥336", "¥198", "16.5元/月"),
            VipRechargeOption("3个月", "¥84", "¥68", "22.7元/月"),
            VipRechargeOption("1个月", null, "¥28", "限时特惠")
        )

        viewBind.rvPrice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBind.rvPrice.adapter = VipRechargeAdapter(options) { option ->
            selectedOption = option
            viewBind.btnCta.text = "${option.currentPrice} 解锁“谁喜欢我”特权"
            if (option.isContinuous) {
                viewBind.tvAgreement.text = "当前为自动续费产品，可随时关闭《自动续费服务协议》"
            } else {
                viewBind.tvAgreement.text = "阅读《常见问题》与相关服务协议"
            }
        }

        selectedOption = options.first()
        viewBind.btnCta.text = "${options.first().currentPrice} 解锁“谁喜欢我”特权"

        viewBind.btnCta.setOnClickListener {
            // TODO: 调微信 / 支付宝支付弹窗
        }
        viewBind.tvAgreement.setOnClickListener {
            // TODO: 打开 WebView 展示 协议 / 常见问题
        }
    }
}


