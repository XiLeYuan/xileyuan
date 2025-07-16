package com.xly.business.recommend.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.view.adapter.CardAdapter
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardStackLayoutManager = CardStackLayoutManager(requireActivity())
//        cardStackLayoutManager.setStackFrom(StackFrom.None)
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL)
        viewBind.cardStackView.layoutManager = cardStackLayoutManager

        // 每张卡片的数据（每张卡片内有20个条目）
        val cardData = List(20) { index ->
            List(20) { "卡片${index + 1} - 列表项${it + 1}" }
        }
        viewBind.cardStackView.adapter = CardAdapter(cardData)
    }


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