package com.xly.business.square.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.business.square.model.Matchmaker
import com.xly.business.square.view.adapter.MatchmakerAdapter
import com.xly.databinding.FragmentMatchmakerBinding
import com.xly.middlelibrary.utils.MatchmakerMockData

class MatchmakerFragment : LYBaseFragment<FragmentMatchmakerBinding, RecommendViewModel>() {

    private lateinit var matchmakerAdapter: MatchmakerAdapter
    private val layoutType = LayoutType.LIST  // LIST 或 GRID

    enum class LayoutType {
        LIST, GRID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupRecyclerView()
        loadData()
        setupListeners()
    }

    private fun initViews() {
        // 初始化搜索框（可选）
    }

    private fun setupRecyclerView() {
        // 根据布局类型设置 LayoutManager
        viewBind.recyclerView.layoutManager = when (layoutType) {
            LayoutType.LIST -> LinearLayoutManager(requireContext())
            LayoutType.GRID -> GridLayoutManager(requireContext(), 2)
        }

        // 设置适配器
        matchmakerAdapter = MatchmakerAdapter(
            onItemClick = { matchmaker ->
                // 点击红娘卡片，跳转到用户资源列表
                navigateToUserList(matchmaker)
            },
            onViewDetailsClick = { matchmaker ->
                // 点击查看用户资源按钮
                navigateToUserList(matchmaker)
            },
            onContactClick = { matchmaker ->
                // 点击联系红娘按钮
                contactMatchmaker(matchmaker)
            }
        )

        viewBind.recyclerView.adapter = matchmakerAdapter

        // 设置分页加载
        setupPagination()
    }

    private fun setupPagination() {
        viewBind.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    ?: return

                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                /*if (lastVisibleItem >= totalItemCount - 3 && !viewModel.isLoading.value) {
                    loadMore()
                }*/
            }
        })
    }

    private fun setupListeners() {
        // 下拉刷新
        viewBind.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }


    private fun loadData() {
        // 方式一：直接从 Mock 数据获取
        val mockData = MatchmakerMockData.generateMatchmakers()
        matchmakerAdapter.submitList(mockData)

    }

    private fun refreshData() {
//        viewModel.refreshMatchmakers()
        viewBind.swipeRefresh.isRefreshing = false
    }

    private fun loadMore() {
//        viewModel.loadMoreMatchmakers()
    }

    private fun searchMatchmakers(keyword: String) {
//        viewModel.searchMatchmakers(keyword)
    }

    private fun navigateToUserList(matchmaker: Matchmaker) {
        // 跳转到用户资源列表页面
        MatchmakerUserResourcesActivity.start(requireContext(), matchmaker)
    }

    private fun contactMatchmaker(matchmaker: Matchmaker) {
        // TODO: 实现联系红娘功能，可以跳转到聊天页面或显示联系方式
        // 例如：跳转到聊天页面
        // ChatActivity.start(requireContext(), matchmaker.id)
    }

    override fun initObservers() {
        /*viewModel.matchmakersLiveData.observe(viewLifecycleOwner, Observer { matchmakers ->
            if (matchmakers.isEmpty()) {
                viewBind.emptyLayout.visibility = View.VISIBLE
                viewBind.recyclerView.visibility = View.GONE
            } else {
                viewBind.emptyLayout.visibility = View.GONE
                viewBind.recyclerView.visibility = View.VISIBLE
                matchmakerAdapter.submitList(matchmakers)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // 显示/隐藏加载状态
        })*/
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
    ): FragmentMatchmakerBinding {
        return FragmentMatchmakerBinding.inflate(inflater, container, false)
    }

    override fun initViewModel(): RecommendViewModel {
        return ViewModelProvider(this)[RecommendViewModel::class.java]
    }
}