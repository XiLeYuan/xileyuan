package com.xly.business.find.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentFindBinding

class FindFragment : LYBaseFragment<FragmentFindBinding,RecommendViewModel>() {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
            }
        })
    }


    override fun initView() {

    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindBinding {
        return FragmentFindBinding.inflate(layoutInflater)
    }



    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
}