package com.xly.business.square.view

import com.xly.base.LYBaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.business.square.model.TodaySelectionUser
import com.xly.business.square.view.adapter.TodaySelectionAdapter
import com.xly.business.square.viewmodel.TodaySelectionViewModel
import com.xly.databinding.FragmentTodaySelectionBinding
import java.text.SimpleDateFormat
import java.util.*

class CherryPickFragment : LYBaseFragment<FragmentTodaySelectionBinding, TodaySelectionViewModel>() {

    private lateinit var selectionAdapter: TodaySelectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupRecyclerView()
        loadData()
        setupListeners()
    }

    private fun initViews() {
        // 设置更新时间
    }

    private fun setupRecyclerView() {
        viewBind.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        selectionAdapter = TodaySelectionAdapter(
            onItemClick = { user ->
                // 点击卡片，跳转到用户详情
                navigateToUserDetail(user)
            },
            onLikeClick = { user ->
                // 点击喜欢按钮
                handleLike(user)
            }
        )

        viewBind.recyclerView.adapter = selectionAdapter
    }

    private fun setupListeners() {
        // 下拉刷新
        viewBind.swipeRefresh.setOnRefreshListener {
            refreshData()
        }


    }

    private fun loadData() {
        viewModel.loadTodaySelection()
    }

    private fun refreshData() {
        viewModel.refreshTodaySelection()
        viewBind.swipeRefresh.isRefreshing = false
    }



    private fun navigateToUserDetail(user: TodaySelectionUser) {
        // 转换为 UserCard 并跳转
        /*val userCard = convertToUserCard(user)
        val intent = Intent(requireContext(), UserDetailActivity::class.java).apply {
            putExtra("user_card", userCard)
        }
        startActivity(intent)*/
    }

    private fun handleLike(user: TodaySelectionUser) {
        viewModel.likeUser(user.id)
        // 显示提示
        Toast.makeText(requireContext(), "已添加到喜欢列表", Toast.LENGTH_SHORT).show()
    }

    private fun convertToUserCard(user: TodaySelectionUser): com.jspp.model.UserCard {
        return com.jspp.model.UserCard(
            id = user.id,
            name = user.name,
            age = user.age,
            location = user.location,
            avatarUrl = user.avatar,
            bio = user.selectionDescription,
            tags = user.tags
        )
    }

    override fun initObservers() {
        viewModel.selectionUsersLiveData.observe(viewLifecycleOwner, Observer { users ->
            if (users.isEmpty()) {
                viewBind.emptyLayout.visibility = View.VISIBLE
                viewBind.recyclerView.visibility = View.GONE
            } else {
                viewBind.emptyLayout.visibility = View.GONE
                viewBind.recyclerView.visibility = View.VISIBLE
                selectionAdapter.submitList(users)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            viewBind.swipeRefresh.isRefreshing = isLoading
        })
    }

    override fun initView() {
        // 初始化视图
    }

    override fun initOnClick() {
        // 初始化点击事件
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTodaySelectionBinding {
        return FragmentTodaySelectionBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): TodaySelectionViewModel {
        return ViewModelProvider(this)[TodaySelectionViewModel::class.java]
    }
}