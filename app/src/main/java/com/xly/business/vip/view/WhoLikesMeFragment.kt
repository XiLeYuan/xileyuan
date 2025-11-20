package com.xly.business.vip.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.FragmentWhoLikesMeBinding

class WhoLikesMeFragment : Fragment() {

    private var _binding: FragmentWhoLikesMeBinding? = null
    private val binding get() = _binding!!

    private var selectedOption: VipRechargeOption? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhoLikesMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = listOf(
            VipRechargeOption("连续包年", "¥696", "¥318", "26.5元/月", isRecommended = true, isContinuous = true),
            VipRechargeOption("连续包季", "¥174", "¥112", "37.3元/月", isContinuous = true),
            VipRechargeOption("连续包月", "¥58", "¥48", "48元/月", isContinuous = true),
            VipRechargeOption("12个月", "¥336", "¥198", "16.5元/月"),
            VipRechargeOption("3个月", "¥84", "¥68", "22.7元/月"),
            VipRechargeOption("1个月", null, "¥28", "限时特惠")
        )

        binding.rvPrice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPrice.adapter = VipRechargeAdapter(options) { option ->
            selectedOption = option
            binding.btnCta.text = "${option.currentPrice} 解锁“谁喜欢我”特权"
            if (option.isContinuous) {
                binding.tvAgreement.text = "当前为自动续费产品，可随时关闭《自动续费服务协议》"
            } else {
                binding.tvAgreement.text = "阅读《常见问题》与相关服务协议"
            }
        }

        selectedOption = options.first()
        binding.btnCta.text = "${options.first().currentPrice} 解锁“谁喜欢我”特权"

        binding.btnCta.setOnClickListener {
            // TODO: 调微信 / 支付宝支付弹窗
        }
        binding.tvAgreement.setOnClickListener {
            // TODO: 打开 WebView 展示 协议 / 常见问题
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


