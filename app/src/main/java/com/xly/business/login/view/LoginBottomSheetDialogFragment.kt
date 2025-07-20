package com.xly.business.login.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xly.databinding.LayoutLoginBottomSheetBinding

class LoginBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _binding: LayoutLoginBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onLoginClick: (() -> Unit)? = null
    var isAgreeChecked: () -> Boolean = { binding.cbAgree.isChecked }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutLoginBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            onLoginClick?.invoke()
        }
        binding.tvUserProtocol.setOnClickListener {
            val intent = Intent(requireContext(), com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "用户协议")
            intent.putExtra("url", "https://yourdomain.com/user_protocol")
            startActivity(intent)
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            val intent = Intent(requireContext(), com.xly.base.LYWebViewActivity::class.java)
            intent.putExtra("title", "隐私政策")
            intent.putExtra("url", "https://yourdomain.com/privacy_policy")
            startActivity(intent)
        }
        binding.tvUserProtocol.paint.isUnderlineText = true
        binding.tvPrivacyPolicy.paint.isUnderlineText = true
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 