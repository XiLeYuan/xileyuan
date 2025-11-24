package com.xly.business.square.view

import android.content.Intent
import com.xly.base.LYBaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.xly.business.square.model.TodaySelectionUser
import com.xly.business.square.view.adapter.CherryPickAdapter
import com.xly.business.square.viewmodel.CherryPickViewModel
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.FragmentTodaySelectionBinding

class CherryPickFragment : LYBaseFragment<FragmentTodaySelectionBinding, CherryPickViewModel>() {

    private lateinit var selectionAdapter: CherryPickAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupRecyclerView()
        loadData()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // 恢复卡片元素的alpha值（从详情页返回时）
        restoreCardElementsAlpha()
    }

    /**
     * 恢复所有可见卡片元素的alpha值
     */
    private fun restoreCardElementsAlpha() {
        val recyclerView = viewBind.recyclerView
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            ?: return
        
        // 获取可见item的范围
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        
        // 恢复每个可见item的alpha值
        for (i in firstVisible..lastVisible) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
            viewHolder?.itemView?.let { itemView ->
                restoreCardElementAlpha(itemView as? ViewGroup)
            }
        }
    }

    /**
     * 恢复单个卡片元素的alpha值
     */
    private fun restoreCardElementAlpha(cardView: ViewGroup?) {
        cardView?.let {
            val infoContainer = it.findViewById<ViewGroup>(com.xly.R.id.llInfoContainer)
            val rightButtons = it.findViewById<ViewGroup>(com.xly.R.id.llRightButtons)
            val featureTags = it.findViewById<ViewGroup>(com.xly.R.id.llFeatureTags)
            val gradientShadow = it.findViewById<View>(com.xly.R.id.vGradientShadow)
            
            val viewsToRestore = listOfNotNull(
                infoContainer,
                rightButtons,
                featureTags,
                gradientShadow
            )
            
            com.xly.middlelibrary.utils.fadeInViews(viewsToRestore, duration = 200)
        }
    }

    private fun initViews() {
        // 设置更新时间
    }

    private fun setupRecyclerView() {
        viewBind.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        selectionAdapter = CherryPickAdapter(
            onItemClick = { user, avatarView ->
                // 点击卡片，跳转到用户详情（带转场动画）
                navigateToUserDetail(user, avatarView)
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



    private fun navigateToUserDetail(user: TodaySelectionUser, avatarView: View) {
        // 点击用户卡片，跳转到用户详情页（带转场动画）
        val intent = Intent(requireActivity(), LYUserDetailInfoActivity::class.java).apply {
            putExtra("user_id", user.id)
            putExtra("user_name", user.name)
            putExtra("user_avatar", user.avatar)
        }

        // 找到卡片容器，准备淡出非图片元素
        val cardView = avatarView.parent?.parent?.parent as? ViewGroup
        if (cardView != null) {
            // 找到需要淡出的元素容器
            val infoContainer = cardView.findViewById<ViewGroup>(com.xly.R.id.llInfoContainer)
            val rightButtons = cardView.findViewById<ViewGroup>(com.xly.R.id.llRightButtons)
            val featureTags = cardView.findViewById<ViewGroup>(com.xly.R.id.llFeatureTags)
            val gradientShadow = cardView.findViewById<View>(com.xly.R.id.vGradientShadow)
            
            val viewsToFade = listOfNotNull(
                infoContainer,
                rightButtons,
                featureTags,
                gradientShadow
            )
            
            // 淡出元素，然后启动转场动画
            com.xly.middlelibrary.utils.fadeOutViews(viewsToFade, duration = 150) {
                // 创建共享元素转场动画
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    Pair.create(avatarView, "user_avatar_${user.id}")
                )
                startActivity(intent, options.toBundle())
            }
        } else {
            // 如果找不到容器，直接启动转场
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair.create(avatarView, "user_avatar_${user.id}")
            )
            startActivity(intent, options.toBundle())
        }
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

    override fun initViewModel(): CherryPickViewModel {
        return ViewModelProvider(this)[CherryPickViewModel::class.java]
    }
}