package com.xly.business.vip.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.business.vip.model.VipRechargeOption
import com.xly.business.vip.view.adapter.VipRechargeAdapter
import com.xly.databinding.FragmentSvipTabBinding

class SvipTabFragment : Fragment() {

    private var _binding: FragmentSvipTabBinding? = null
    private val binding get() = _binding!!

    private var selectedOption: VipRechargeOption? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSvipTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = listOf(
            VipRechargeOption("连续包年", "¥996", "¥598", "49.8元/月", isRecommended = true, isContinuous = true),
            VipRechargeOption("连续包季", "¥288", "¥188", "62.7元/月", isContinuous = true),
            VipRechargeOption("连续包月", "¥98", "¥78", "78元/月", isContinuous = true)
        )

        binding.rvPrice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPrice.adapter = VipRechargeAdapter(options) { option ->
            selectedOption = option
            binding.btnCta.text = "${option.currentPrice} 解锁SVIP特权"
        }

        selectedOption = options.first()
        binding.btnCta.text = "${options.first().currentPrice} 解锁SVIP特权"

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


