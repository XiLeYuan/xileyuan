package com.xly.business.vip.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.base.LYBaseFragment
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.FragmentSvipTabBinding
import com.xly.index.viewmodel.MainViewModel

class SvipTabFragment : LYBaseFragment<FragmentSvipTabBinding, MainViewModel>() {

    private var selectedOption: VipRechargeOption? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSvipTabBinding {
        return FragmentSvipTabBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun initView() {
        super.initView()
        val options = listOf(
            VipRechargeOption("连续包年", "¥996", "¥598", "49.8元/月", isRecommended = true, isContinuous = true),
            VipRechargeOption("连续包季", "¥288", "¥188", "62.7元/月", isContinuous = true),
            VipRechargeOption("连续包月", "¥98", "¥78", "78元/月", isContinuous = true)
        )

        viewBind.rvPrice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewBind.rvPrice.adapter = VipRechargeAdapter(options) { option ->
            selectedOption = option
            viewBind.btnCta.text = "${option.currentPrice} 解锁SVIP特权"
        }

        selectedOption = options.first()
        viewBind.btnCta.text = "${options.first().currentPrice} 解锁SVIP特权"

        viewBind.btnCta.setOnClickListener {
            // TODO: 调用支付
        }
        viewBind.tvAgreement.setOnClickListener {
            // TODO: 打开 WebView 展示 协议 / 常见问题
        }
    }
}


