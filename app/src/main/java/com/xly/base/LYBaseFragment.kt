package com.xly.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class LYBaseFragment <VB : ViewBinding, VM : ViewModel> : Fragment() {

    protected lateinit var viewBind: VB
    protected lateinit var viewModel: VM

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun initViewModel(): VM


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBind = inflateBinding(inflater, container)
        initView()
        initOnClick()
        return viewBind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = initViewModel()
    }

    open fun initView() {}

    open fun initOnClick() {}

}

