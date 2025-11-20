package com.xly.business.vip.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.FragmentVipTabBinding

class VipTabFragment : Fragment() {

    private var _binding: FragmentVipTabBinding? = null
    private val binding get() = _binding!!

    private var selectedOption: VipRechargeOption? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVipTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = listOf(
            VipRechargeOption("连续包年", "¥696", "¥318", "26.5元/月", isRecommended = true, isContinuous = true),
            VipRechargeOption("连续包季", "¥174", "¥112", "37.3元/月", isContinuous = true),
            VipRechargeOption("连续包月", "¥58", "¥48", "48元/月", isContinuous = true)
        )

        binding.rvPrice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPrice.adapter = VipRechargeAdapter(options) { option ->
            selectedOption = option
            binding.btnCta.text = "${option.currentPrice} 解锁VIP特权"
        }

        selectedOption = options.first()
        binding.btnCta.text = "${options.first().currentPrice} 解锁VIP特权"

        binding.btnCta.setOnClickListener {
            // TODO: 调用支付
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


