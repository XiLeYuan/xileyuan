package com.xly.business.recommend.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.R
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.FragmentHometownBinding
import com.xly.databinding.ItemHometownUserBinding

class HometownFragment : Fragment() {

    private var _binding: FragmentHometownBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HometownAdapter

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
        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = HometownAdapter { user, avatarView ->
            showUserDetail(user, avatarView)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        // 模拟数据
        val avatarResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )
        
        val users = (1..20).map { index ->
            HometownUser(
                id = index.toString(),
                name = "用户$index",
                age = (20 + index % 20),
                avatar = avatarResources[index % avatarResources.size]
            )
        }
        adapter.submitList(users)
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
        val avatar: String
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

                // 设置姓名和年龄
                binding.tvName.text = user.name
                binding.tvAge.text = "${user.age}岁"

                // 点击事件
                binding.root.setOnClickListener {
                    onItemClick(user, binding.ivAvatar)
                }
            }
        }
    }
}

