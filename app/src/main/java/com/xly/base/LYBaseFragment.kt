package com.xly.base

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import com.xly.R
import com.xly.middlelibrary.utils.UmengHelper

abstract class LYBaseFragment <VB : ViewBinding, VM : ViewModel> : Fragment() {

    protected lateinit var viewBind: VB
    protected lateinit var viewModel: VM

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun initViewModel(): VM


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBind = inflateBinding(inflater, container)
        initObservers()
        initView()
        initOnClick()
        return viewBind.root
    }
    
    override fun onResume() {
        super.onResume()
        // 友盟页面统计 - 页面开始（Fragment可选）
        UmengHelper.onPageStart(requireContext(), getPageName())
    }
    
    override fun onPause() {
        super.onPause()
        // 友盟页面统计 - 页面结束（Fragment可选）
        UmengHelper.onPageEnd(requireContext(), getPageName())
    }
    
    /**
     * 获取页面名称，用于友盟统计
     * 子类可以重写此方法自定义页面名称
     * 默认使用类名
     */
    protected open fun getPageName(): String {
        return this::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = initViewModel()
    }

    open fun initView() {}

    open fun initOnClick() {}

    open fun initObservers() {}

    protected fun showToast(str: String) {
        val layout = layoutInflater.inflate(R.layout.view_toast_custom, null)
        val tvToast: TextView = layout.findViewById(R.id.toast_text)
        tvToast.setText(str)
        val toast = Toast(requireActivity())
        toast.setDuration(Toast.LENGTH_SHORT)
        toast.setView(layout)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        initTabBar()
    }


    private fun initTabBar() {
        ImmersionBar.with(requireActivity())
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.white)
            .navigationBarDarkIcon(true)
            .init()
    }

    fun showLoading() {
        if (!requireActivity().isFinishing && !requireActivity().isDestroyed) {

        }
    }

    fun cancelLoading() {
        if (!requireActivity().isFinishing && !requireActivity().isDestroyed) {

        }
    }

}

