package com.xly.business.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.xly.databinding.FragmentLikedPeopleBinding

class LikedPeopleFragment : Fragment() {

    private var _binding: FragmentLikedPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikedPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // TODO: 设置适配器，显示喜欢的人列表
        binding.emptyView.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun loadData() {
        // TODO: 加载喜欢的人数据
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

