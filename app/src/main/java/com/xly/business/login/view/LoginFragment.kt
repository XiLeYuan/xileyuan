package com.xly.business.login.view

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.login.viewmodel.LoginViewModel
import com.xly.databinding.FragmentLoginBinding
import com.xly.databinding.LayoutLoginBottomSheetBinding

class LoginFragment : LYBaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private var loginDialog: LoginBottomSheetDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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