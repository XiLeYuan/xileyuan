package com.xly.business.recommend.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRecommendBinding {
        return FragmentRecommendBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}