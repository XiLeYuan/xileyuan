package com.xly.business.recommend.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.google.android.material.appbar.AppBarLayout
import com.xly.base.LYBaseActivity
import com.xly.business.recommend.model.UserDetailItem
import com.xly.business.user.UserInfo
import com.xly.databinding.ActivityUserDetailBinding
import com.xly.index.viewmodel.MainViewModel
import com.xly.business.recommend.view.adapter.UserDetailAdapter

class UserDetailActivity : LYBaseActivity<ActivityUserDetailBinding,MainViewModel>() {

    companion object {
        const val EXTRA_USER_INFO = "extra_user_info"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_TRANSITION_NAME = "extra_transition_name"

        fun start(activity: Activity, userInfo: UserInfo, imageUrl: String, transitionName: String) {
            val intent = Intent(activity, UserDetailActivity::class.java).apply {
                putExtra(EXTRA_USER_INFO, userInfo)
                putExtra(EXTRA_IMAGE_URL, imageUrl)
                putExtra(EXTRA_TRANSITION_NAME, transitionName)
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                activity.findViewById(android.R.id.content),
                transitionName
            )

            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }

    private lateinit var userDetailAdapter: UserDetailAdapter
    private var userInfo: UserInfo? = null
    private var imageUrl: String? = null
    private var transitionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        // 获取传递的数据
        userInfo = intent.getParcelableExtra(EXTRA_USER_INFO)
        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        transitionName = intent.getStringExtra(EXTRA_TRANSITION_NAME)
        Log.i("UserDetailActivity", "userInfo: $userInfo")
        initViews()
        setupToolbar()
        setupRecyclerView()
        loadUserData()

    }



    private fun initViews() {
        // 设置转场动画名称
        transitionName?.let { name ->
            ViewCompat.setTransitionName(viewBind.ivUserCover, name)
        }

        // 设置图片
        imageUrl?.let { url ->
            // 这里使用你的图片加载库，比如Glide
            // Glide.with(this).load(url).into(iv_user_cover)
            viewBind.ivUserCover.setImageResource(R.mipmap.head_img) // 临时使用默认图片
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(viewBind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewBind.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // 设置折叠工具栏
        viewBind.collapsingToolbar.title = userInfo?.name ?: "用户详情"
        viewBind.collapsingToolbar.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
        viewBind.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(android.R.color.white))

        // 设置AppBarLayout的滚动监听，实现视差效果
        viewBind.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                val scrollRange = appBarLayout?.totalScrollRange ?: 0
                val progress = 1.0f + verticalOffset.toFloat() / scrollRange

                // 图片视差效果
                viewBind.ivUserCover.translationY = verticalOffset * 0.5f

                // 透明度变化
                viewBind.ivUserCover.alpha = 0.3f + 0.7f * progress
            }
        })
    }

    private fun setupRecyclerView() {
        userDetailAdapter = UserDetailAdapter()
        viewBind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@UserDetailActivity)
            adapter = userDetailAdapter
            setHasFixedSize(true)

            // 添加滚动监听，实现更精细的视差效果
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 当RecyclerView滚动时，调整图片位置
                    val firstVisibleItem = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem == 0) {
                        val firstView = layoutManager!!.findViewByPosition(0)
                        firstView?.let { view ->
                            val top = view.top
                            viewBind.ivUserCover.translationY = top * 0.3f
                        }
                    }
                }
            })
        }
    }

    private fun loadUserData() {
        userInfo?.let { user ->
            // 创建详情数据列表
            val detailItems = mutableListOf<UserDetailItem>()

            // 基本信息
            detailItems.add(UserDetailItem("基本信息", "", UserDetailItem.TYPE_HEADER))
            detailItems.add(UserDetailItem("姓名", user.name, UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("年龄", "${user.age}岁", UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("身高", "${user.height}cm", UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("体重", "${user.weight}kg", UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("职业", user.occupation, UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("学历", user.education, UserDetailItem.TYPE_INFO))

            // 兴趣爱好
            detailItems.add(UserDetailItem("兴趣爱好", "", UserDetailItem.TYPE_HEADER))
            user.hobbies?.forEach { hobby ->
                detailItems.add(UserDetailItem("", hobby, UserDetailItem.TYPE_HOBBY))
            }

            // 个人介绍
            detailItems.add(UserDetailItem("个人介绍", "", UserDetailItem.TYPE_HEADER))
            detailItems.add(UserDetailItem("", user.bio ?: "这个人很懒，什么都没写~", UserDetailItem.TYPE_BIO))

            // 联系方式
            detailItems.add(UserDetailItem("联系方式", "", UserDetailItem.TYPE_HEADER))
            detailItems.add(UserDetailItem("微信", user.wechat ?: "暂无", UserDetailItem.TYPE_INFO))
            detailItems.add(UserDetailItem("QQ", user.qq ?: "暂无", UserDetailItem.TYPE_INFO))

            userDetailAdapter.setData(detailItems)
        }
    }

    override fun onBackPressed() {
        // 支持返回动画
        super.onBackPressed()
    }


    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityUserDetailBinding {
        return ActivityUserDetailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

}