package com.xly.business.recommend.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.R
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.FragmentHometownBinding
import com.xly.databinding.ItemHometownUserBinding

class HometownFragment : Fragment() {

    private var _binding: FragmentHometownBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HometownAdapter
    private var currentPage = 1
    private val pageSize = 20
    private var hasMoreData = true
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHometownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshLayout()
        setupRecyclerView()
        loadData(isRefresh = true)
    }

    private fun setupRefreshLayout() {
        binding.refreshLayout.setRefreshHeader(MaterialHeader(requireActivity()))
        binding.refreshLayout.setRefreshFooter(ClassicsFooter(requireActivity()))
        binding.refreshLayout.setEnableRefresh(true)
        binding.refreshLayout.setEnableLoadMore(true)

        // 下拉刷新监听
        binding.refreshLayout.setOnRefreshListener { refreshLayout ->
            if (!isLoading) {
                loadData(isRefresh = true) {
                    refreshLayout.finishRefresh()
                }
            } else {
                refreshLayout.finishRefresh()
            }
        }

        // 上拉加载更多监听
        binding.refreshLayout.setOnLoadMoreListener { refreshLayout ->
            if (!isLoading && hasMoreData) {
                loadData(isRefresh = false) {
                    refreshLayout.finishLoadMore(hasMoreData)
                }
            } else {
                refreshLayout.finishLoadMore(!hasMoreData)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HometownAdapter { user, avatarView ->
            showUserDetail(user, avatarView)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun loadData(isRefresh: Boolean, onComplete: (() -> Unit)? = null) {
        if (isLoading) {
            onComplete?.invoke()
            return
        }

        isLoading = true

        if (isRefresh) {
            currentPage = 1
            hasMoreData = true
        }

        // 模拟网络请求延迟
        binding.recyclerView.postDelayed({
            // 模拟数据
            val avatarResources = listOf(
                "head_one", "head_two", "head_three", "head_four",
                "head_five", "head_six", "head_seven", "head_eight"
            )
            
            val tagsList = listOf(
                listOf("本科", "5k-8k"),
                listOf("硕士", "10k-15k"),
                listOf("本科", "8k-12k"),
                listOf("博士", "15k-20k"),
                listOf("本科", "3k-5k")
            )
            
            val startIndex = if (isRefresh) 1 else (currentPage - 1) * pageSize + 1
            val endIndex = startIndex + pageSize - 1
            val users = (startIndex..endIndex).map { index ->
                HometownUser(
                    id = index.toString(),
                    name = "用户$index",
                    age = (20 + index % 20),
                    avatar = avatarResources[index % avatarResources.size],
                    isVerified = index % 3 == 0, // 每3个用户中有一个认证
                    tags = tagsList[index % tagsList.size]
                )
            }
            
            if (isRefresh) {
                adapter.submitList(users)
            } else {
                val currentList = adapter.getCurrentList().toMutableList()
                currentList.addAll(users)
                adapter.submitList(currentList)
            }
            
            currentPage++
            // 模拟数据加载完毕（第5页后没有更多数据）
            hasMoreData = currentPage <= 5
            
            isLoading = false
            onComplete?.invoke()
        }, 800) // 模拟800ms网络延迟
    }

    private fun showUserDetail(user: HometownUser, avatarView: View? = null) {
        val intent = Intent(requireActivity(), LYUserDetailInfoActivity::class.java).apply {
            putExtra("user_id", user.id)
            putExtra("user_name", user.name)
            putExtra("user_avatar", user.avatar)
        }

        if (avatarView != null) {
            val transitionName = "user_avatar_${user.id}"
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair.create(avatarView, transitionName)
            )
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class HometownUser(
        val id: String,
        val name: String,
        val age: Int,
        val avatar: String,
        val isVerified: Boolean = false,
        val tags: List<String> = emptyList()
    )

    class HometownAdapter(
        private val onItemClick: (HometownUser, View) -> Unit
    ) : RecyclerView.Adapter<HometownAdapter.HometownViewHolder>() {

        private val users = mutableListOf<HometownUser>()

        fun submitList(newUsers: List<HometownUser>) {
            users.clear()
            users.addAll(newUsers)
            notifyDataSetChanged()
        }

        fun getCurrentList(): List<HometownUser> {
            return users.toList()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HometownViewHolder {
            val binding = ItemHometownUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return HometownViewHolder(binding)
        }

        override fun onBindViewHolder(holder: HometownViewHolder, position: Int) {
            holder.bind(users[position], onItemClick)
        }

        override fun getItemCount() = users.size

        class HometownViewHolder(
            private val binding: ItemHometownUserBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(user: HometownUser, onItemClick: (HometownUser, View) -> Unit) {
                // 加载头像
                val context = binding.root.context
                val resourceId = context.resources.getIdentifier(
                    user.avatar,
                    "mipmap",
                    context.packageName
                )
                if (resourceId != 0) {
                    Glide.with(context)
                        .load(resourceId)
                        .centerCrop()
                        .into(binding.ivAvatar)
                } else {
                    binding.ivAvatar.setImageResource(R.mipmap.head_img)
                }

                // 设置转场动画名称
                binding.ivAvatar.transitionName = "user_avatar_${user.id}"

                // 设置姓名（年龄和认证标识在布局中已经通过逗号连接）
                binding.tvName.text = user.name
                binding.tvAge.text = "${user.age}岁"
                
                // 显示/隐藏逗号分隔符
                binding.tvNameAgeSeparator.visibility = View.VISIBLE
                
                // 显示/隐藏认证标识
                binding.ivVerified.visibility = if (user.isVerified) View.VISIBLE else View.GONE

                // 设置标签
                setupTags(binding.llTags, user.tags)

                // 点击事件
                binding.root.setOnClickListener {
                    onItemClick(user, binding.ivAvatar)
                }
            }

            private fun setupTags(container: ViewGroup, tags: List<String>) {
                container.removeAllViews()
                
                if (tags.isNotEmpty()) {
                    container.visibility = View.VISIBLE
                    tags.take(2).forEach { tag -> // 最多显示2个标签
                        val tagView = TextView(container.context).apply {
                            text = tag
                            textSize = 11f
                            setTextColor(ContextCompat.getColor(
                                context,
                                R.color.text_secondary
                            ))
                            background = ContextCompat.getDrawable(
                                context,
                                R.drawable.tag_background
                            )
                            setPadding(8.dpToPx(), 4.dpToPx(), 8.dpToPx(), 4.dpToPx())

                            layoutParams = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                marginEnd = 4.dpToPx()
                            }
                        }
                        container.addView(tagView)
                    }
                } else {
                    container.visibility = View.GONE
                }
            }

            private fun Int.dpToPx(): Int {
                return (this * binding.root.context.resources.displayMetrics.density).toInt()
            }
        }
    }
}

