package com.xly.business.recommend.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
                Toast.makeText(requireContext(),"加载失败: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

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