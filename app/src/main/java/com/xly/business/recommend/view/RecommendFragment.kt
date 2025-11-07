package com.xly.business.recommend.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jspp.activity.HomeActivity
import com.jspp.activity.TestFloatingCardActivity
import com.jspp.activity.UserDetailActivity
import com.jspp.adapter.UserCardAdapter
import com.jspp.model.UserCard
import com.xly.base.LYBaseFragment
import com.xly.business.recommend.viewmodel.RecommendViewModel
import com.xly.databinding.FragmentRecommendBinding
import com.xly.R
import com.xly.business.user.CheeseDetailActivity
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.middlelibrary.utils.click
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod


class RecommendFragment : LYBaseFragment<FragmentRecommendBinding,RecommendViewModel>() {



    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var userCardAdapter: UserCardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardStackView()
        loadCardData()
    }


    private fun setupCardStackView() {
        // 创建布局管理器
        cardStackLayoutManager = CardStackLayoutManager(requireActivity(), object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {
                // 卡片拖动中
                updateButtonScale(direction, ratio)
            }

            override fun onCardSwiped(direction: Direction) {
                // 卡片滑动完成
                when (direction) {
                    Direction.Left -> {
                    }
                    Direction.Right -> {
                    }
                    Direction.Top -> {
                    }
                    Direction.Bottom -> {
                    }
                }
            }

            override fun onCardRewound() {
                // 卡片回退
            }

            override fun onCardCanceled() {
                // 卡片取消滑动
            }

            // 卡片出现
            override fun onCardAppeared(view: View, position: Int) {}

            override fun onCardDisappeared(view: View, position: Int) {
                // 卡片消失
            }
        }).apply {
            // 设置堆叠方向
            setStackFrom(StackFrom.Top)
            // 设置可见卡片数量
            setVisibleCount(3)
            // 设置缩放比例
            setScaleInterval(0.95f)
            // 设置透明度
            setMaxDegree(20f)
            // 设置移动距离
            setTranslationInterval(8.0f)
            // 设置滑动方向
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            // 设置可滑动的方向
            setSwipeThreshold(0.3f)
            // 设置最大角度
            setMaxDegree(50f)
            // 设置方向
            setDirections(Direction.HORIZONTAL)

            // 设置是否可以手动滑动
            setCanScrollHorizontal(true)
            setCanScrollVertical(false)
        }

        // 创建适配器
        userCardAdapter = UserCardAdapter { userCard, cardView ->
            showUserDetail(userCard, cardView)
        }

        // 设置布局管理器和适配器
        viewBind.cardStackView.layoutManager = cardStackLayoutManager
        viewBind.cardStackView.adapter = userCardAdapter
    }


    override fun initObservers() {
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { user ->

            }.onFailure { error ->
                Toast.makeText(requireContext(),"加载失败: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun initView() {
        viewBind.topNav.topImgOne.setImageResource(R.mipmap.shaixuan_selecte_icon)
    }

    override fun initOnClick() {
        viewBind.chatImg.click {
            HomeActivity.start(requireActivity())
        }
        viewBind.likeIv.click {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            viewBind.cardStackView.swipe()

        }
        viewBind.unLikeIv.click {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            viewBind.cardStackView.swipe()

        }
    }

    private fun showUserDetail(userCard: UserCard, cardView: View? = null) {

        val intent = Intent(requireActivity(), CheeseDetailActivity::class.java).apply {
            putExtra("user_card", userCard)
        }

        if (cardView != null) {
            Log.i("cardView","NO-NULL")
            // 使用共享元素转场动画
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                cardView,
                "user_card"
            )
            startActivity(intent, options.toBundle())
        } else {
            Log.i("cardView","NULL")
            // 普通转场
            startActivity(intent)
        }
    }


    private fun loadCardData() {
        // 模拟加载卡片数据
        val cards = listOf(
            UserCard(
                id = "1",
                name = "小美",
                age = 25,
                location = "北京",
                avatarUrl = "https://example.com/avatar1.jpg",
                bio = "喜欢旅行、摄影、音乐，希望找到一个志同道合的伴侣。",
                tags = listOf("163cm", "本科", "5k-8k")
            ),
            UserCard(
                id = "2",
                name = "小雨",
                age = 23,
                location = "上海",
                avatarUrl = "https://example.com/avatar2.jpg",
                bio = "热爱生活，喜欢尝试新事物，希望能遇到有趣的人。",
                tags = listOf("178cm", "本科", "2w-3w")
            ),
            UserCard(
                id = "3",
                name = "小芳",
                age = 26,
                location = "深圳",
                avatarUrl = "https://example.com/avatar3.jpg",
                bio = "工作认真，生活简单，希望能找到一个温暖的人。",
                tags = listOf("工作", "电影", "咖啡")
            ),
            UserCard(
                id = "4",
                name = "小丽",
                age = 24,
                location = "广州",
                avatarUrl = "https://example.com/avatar4.jpg",
                bio = "活泼开朗，喜欢交朋友，希望能遇到对的人。",
                tags = listOf("交友", "游戏", "美食")
            ),
            UserCard(
                id = "5",
                name = "小雅",
                age = 27,
                location = "杭州",
                avatarUrl = "https://example.com/avatar5.jpg",
                bio = "文艺青年，喜欢看书、听音乐，希望能遇到懂我的人。",
                tags = listOf("阅读", "音乐", "文艺")
            )
        )

        userCardAdapter.setCards(cards)
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



    /*********************************************/


    private val defaultAlpha = 0.5f      // 默认透明度
    private val maxAlpha = 1.0f          // 最大透明度
    private val minAlpha = 0.3f          // 最小透明度

    /*** 根据滑动方向和距离更新按钮缩放和透明度*/
    private fun updateButtonScale(direction: Direction, ratio: Float) {
        when (direction) {
            Direction.Right -> {
                // 向右滑动 - 喜欢按钮放大，不喜欢按钮缩小
                val likeAlpha = defaultAlpha + (maxAlpha - defaultAlpha) * ratio
                val dislikeAlpha = defaultAlpha - (defaultAlpha - minAlpha) * ratio

                animateButton(viewBind.likeIv, likeAlpha)
                animateButton(viewBind.unLikeIv, dislikeAlpha)
            }
            Direction.Left -> {
                // 向左滑动 - 不喜欢按钮放大，喜欢按钮缩小
                val dislikeAlpha = defaultAlpha + (maxAlpha - defaultAlpha) * ratio
                val likeAlpha = defaultAlpha - (defaultAlpha - minAlpha) * ratio

                animateButton(viewBind.unLikeIv, dislikeAlpha)
                animateButton(viewBind.likeIv, likeAlpha)
            }
            else -> {
                resetButtonState()
            }
        }
    }

    /**
     * 动画更新按钮的缩放和透明度
     */
    private fun animateButton(button: View, alpha: Float) {
        button.alpha = alpha
    }

    /**
     * 重置按钮状态到默认值
     */
    private fun resetButtonState() {
        viewBind.likeIv.alpha = defaultAlpha
        viewBind.unLikeIv.alpha = defaultAlpha
    }

}