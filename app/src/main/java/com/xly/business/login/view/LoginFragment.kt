package com.xly.business.login.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.LYBaseFragment
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.FragmentLoginBinding

class LoginFragment : LYBaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private var loginDialog: LoginBottomSheetDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 设置数字突出显示
        setupHighlightedText()
        // 监听动画进度
        viewModel.showTextIndex.observe(viewLifecycleOwner, Observer { idx ->
            showTextWithAnim(idx)
        })
        viewModel.showDialog.observe(viewLifecycleOwner, Observer { show ->
            if (show == true) {
                showLoginDialog()
            }
        })
        viewBind.root.post { viewModel.startTextAnimation() }
    }
    
    private fun setupHighlightedText() {
        // 突出显示"100000人"中的数字部分
        val text = "已经有100000人"
        val spannable = SpannableString(text)
        
        // 找到数字部分的位置
        val numberStart = text.indexOf("100000")
        val numberEnd = numberStart + "100000".length
        
        if (numberStart >= 0) {
            // 设置数字部分为品牌色
            val brandColor = ContextCompat.getColor(requireContext(), R.color.brand_primary)
            spannable.setSpan(
                ForegroundColorSpan(brandColor),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // 设置数字部分字体更大（1.4倍）
            spannable.setSpan(
                RelativeSizeSpan(1.4f),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            
            // 设置数字部分加粗
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                numberStart,
                numberEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        viewBind.tvLine3.text = spannable
    }

    private fun showLoginDialog() {
        if (loginDialog == null) {
            loginDialog = LoginBottomSheetDialogFragment().apply {
                onLoginClick = myLambda@{
                    if (!isAgreeChecked()) {
                        showToast("请先同意协议")
                        return@myLambda
                    }
                    // 修改: 跳转到手机号登录页
                    val intent = Intent(requireActivity(), PhoneLoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        loginDialog?.show(parentFragmentManager, "LoginBottomSheetDialogFragment")
    }

    private fun showTextWithAnim(idx: Int) {
        val ids = listOf(viewBind.tvLine1, viewBind.tvLine2, viewBind.tvLine3, viewBind.tvLine4)
        ids.forEachIndexed { i, tv ->
            if (i < idx) {
                if (tv.alpha == 0f) {
                    tv.animate().alpha(1f).setDuration(400).start()
                }
            }
        }
    }

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): LoginViewModel {
        return ViewModelProvider(this)[LoginViewModel::class.java]
    }
} 