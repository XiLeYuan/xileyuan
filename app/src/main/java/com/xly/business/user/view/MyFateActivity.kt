package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.ActivityMyFateBinding
import com.xly.databinding.ItemHometownUserBinding
import com.xly.middlelibrary.utils.click

class MyFateActivity : LYBaseActivity<ActivityMyFateBinding, MyFateViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MyFateActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var adapter: MyFateAdapter
    private var currentPage = 1
    private val pageSize = 20
    private var hasMoreData = true
    private var isLoading = false

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityMyFateBinding {
        return ActivityMyFateBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MyFateViewModel {
        return ViewModelProvider(this)[MyFateViewModel::class.java]
    }

    override fun initView() {
        setupRefreshLayout()
        setupRecyclerView()
        loadData(isRefresh = true)
    }

    override fun initOnClick() {
        // 返回按钮
        viewBind.ivBack.click {
            finish()
        }
    }

    private fun setupRefreshLayout() {
        viewBind.refreshLayout.setRefreshHeader(MaterialHeader(this))
        viewBind.refreshLayout.setRefreshFooter(ClassicsFooter(this))
        viewBind.refreshLayout.setEnableRefresh(true)
        viewBind.refreshLayout.setEnableLoadMore(true)

        // 下拉刷新监听
        viewBind.refreshLayout.setOnRefreshListener { refreshLayout ->
            if (!isLoading) {
                loadData(isRefresh = true) {
                    refreshLayout.finishRefresh()
                }
            } else {
                refreshLayout.finishRefresh()
            }
        }

        // 上拉加载更多监听
        viewBind.refreshLayout.setOnLoadMoreListener { refreshLayout ->
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
        adapter = MyFateAdapter { user, avatarView ->
            showUserDetail(user, avatarView)
        }
        viewBind.recyclerView.layoutManager = GridLayoutManager(this, 2)
        viewBind.recyclerView.adapter = adapter
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
        viewBind.recyclerView.postDelayed({
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
                MyFateUser(
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

    private fun showUserDetail(user: MyFateUser, avatarView: View? = null) {
        val intent = Intent(this, LYUserDetailInfoActivity::class.java).apply {
            putExtra("user_id", user.id)
            putExtra("user_name", user.name)
            putExtra("user_avatar", user.avatar)
        }

        if (avatarView != null) {
            val transitionName = "user_avatar_${user.id}"
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair.create(avatarView, transitionName)
            )
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    data class MyFateUser(
        val id: String,
        val name: String,
        val age: Int,
        val avatar: String,
        val isVerified: Boolean = false,
        val tags: List<String> = emptyList()
    )

    class MyFateAdapter(
        private val onItemClick: (MyFateUser, View) -> Unit
    ) : RecyclerView.Adapter<MyFateAdapter.MyFateViewHolder>() {

        private val users = mutableListOf<MyFateUser>()

        fun submitList(newUsers: List<MyFateUser>) {
            users.clear()
            users.addAll(newUsers)
            notifyDataSetChanged()
        }

        fun getCurrentList(): List<MyFateUser> {
            return users.toList()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFateViewHolder {
            val binding = ItemHometownUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MyFateViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyFateViewHolder, position: Int) {
            holder.bind(users[position], onItemClick)
        }

        override fun getItemCount() = users.size

        class MyFateViewHolder(
            private val binding: ItemHometownUserBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(user: MyFateUser, onItemClick: (MyFateUser, View) -> Unit) {
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

class MyFateViewModel : ViewModel() {
    // 可以在这里添加获取我的缘份数据的数据逻辑
}

