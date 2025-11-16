package com.xly.business.recommend.view

import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import com.jspp.adapter.UserCardAdapter
import com.jspp.model.UserCard
import com.xly.business.user.LYUserDetailInfoActivity
import com.xly.databinding.FragmentRecommendContentBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod

class RecommendContentFragment : Fragment() {

    private var _binding: FragmentRecommendContentBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var userCardAdapter: UserCardAdapter

    private val defaultScale = 1.0f
    private val maxScale = 1.2f
    private val minScale = 0.8f
    
    private val defaultAlpha = 0.2f  // 默认背景透明度 20% (#33000000)
    private val maxAlpha = 0.6f      // 最大背景透明度 60%
    private val minAlpha = 0.1f      // 最小背景透明度 10%

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardStackView()
        setupClickListeners()
        loadCardData()
    }

    private fun setupCardStackView() {
        cardStackLayoutManager = CardStackLayoutManager(requireActivity(), object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {
                updateButtonScale(direction, ratio)
            }

            override fun onCardSwiped(direction: Direction) {}

            override fun onCardRewound() {}

            override fun onCardCanceled() {
                resetButtonState()
            }

            override fun onCardAppeared(view: View, position: Int) {
                // 卡片出现时，恢复按钮状态
                resetButtonState()
            }

            override fun onCardDisappeared(view: View, position: Int) {}
        }).apply {
            setStackFrom(StackFrom.Top)
            setVisibleCount(3)
            setScaleInterval(0.95f)
            setMaxDegree(20f)
            setTranslationInterval(8.0f)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setSwipeThreshold(0.3f)
            setMaxDegree(50f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(false)
        }

        userCardAdapter = UserCardAdapter { userCard, cardView ->
            showUserDetail(userCard, cardView)
        }

        binding.cardStackView.layoutManager = cardStackLayoutManager
        binding.cardStackView.adapter = userCardAdapter
    }

    private fun setupClickListeners() {
        binding.likeIv.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()
        }

        binding.unLikeIv.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()
        }
    }

    private fun showUserDetail(userCard: UserCard, avatarView: View? = null) {
        val intent = Intent(requireActivity(), LYUserDetailInfoActivity::class.java).apply {
            putExtra("user_id", userCard.id)
            putExtra("user_name", userCard.name)
            putExtra("user_avatar", userCard.avatarUrl)
        }

        if (avatarView != null) {
            val transitionName = "user_avatar_${userCard.id}"
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair.create(avatarView, transitionName)
            )
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
        }
    }

    private fun loadCardData() {
        val avatarResources = listOf(
            "head_one", "head_two", "head_three", "head_four",
            "head_five", "head_six", "head_seven", "head_eight"
        )

        val cards = listOf(
            UserCard(
                id = "1", name = "小美", age = 25, location = "北京",
                avatarUrl = avatarResources[0], bio = "喜欢旅行、摄影、音乐",
                tags = listOf("163cm", "本科", "5k-8k"),
                hometown = "河南", residence = "北京"
            ),
            UserCard(
                id = "2", name = "小雨", age = 23, location = "上海",
                avatarUrl = avatarResources[1], bio = "热爱生活，喜欢尝试新事物",
                tags = listOf("178cm", "本科", "2w-3w"),
                hometown = "江苏", residence = "上海"
            ),
            UserCard(
                id = "3", name = "小芳", age = 26, location = "深圳",
                avatarUrl = avatarResources[2], bio = "工作认真，生活简单",
                tags = listOf("工作", "电影", "咖啡"),
                hometown = "湖南", residence = "深圳"
            ),
            UserCard(
                id = "4", name = "小丽", age = 24, location = "广州",
                avatarUrl = avatarResources[3], bio = "活泼开朗，喜欢交朋友",
                tags = listOf("交友", "游戏", "美食"),
                hometown = "广东", residence = "广州"
            ),
            UserCard(
                id = "5", name = "小雅", age = 27, location = "杭州",
                avatarUrl = avatarResources[4], bio = "文艺青年，喜欢看书、听音乐",
                tags = listOf("阅读", "音乐", "文艺"),
                hometown = "浙江", residence = "杭州"
            )
        )

        userCardAdapter.setCards(cards)
    }

    private fun updateButtonScale(direction: Direction, ratio: Float) {
        when (direction) {
            Direction.Right -> {
                // 右滑：喜欢按钮放大并加深背景，不喜欢按钮缩小并变浅背景
                val likeScale = defaultScale + (maxScale - defaultScale) * ratio
                val dislikeScale = defaultScale - (defaultScale - minScale) * ratio
                val likeAlpha = defaultAlpha + (maxAlpha - defaultAlpha) * ratio
                val dislikeAlpha = defaultAlpha - (defaultAlpha - minAlpha) * ratio
                (binding.likeIv.parent as? View)?.let {
                    animateButtonScale(it, likeScale)
                    animateButtonBackground(it, likeAlpha)
                }
                (binding.unLikeIv.parent as? View)?.let {
                    animateButtonScale(it, dislikeScale)
                    animateButtonBackground(it, dislikeAlpha)
                }
            }
            Direction.Left -> {
                // 左滑：不喜欢按钮放大并加深背景，喜欢按钮缩小并变浅背景
                val dislikeScale = defaultScale + (maxScale - defaultScale) * ratio
                val likeScale = defaultScale - (defaultScale - minScale) * ratio
                val dislikeAlpha = defaultAlpha + (maxAlpha - defaultAlpha) * ratio
                val likeAlpha = defaultAlpha - (defaultAlpha - minAlpha) * ratio
                (binding.unLikeIv.parent as? View)?.let {
                    animateButtonScale(it, dislikeScale)
                    animateButtonBackground(it, dislikeAlpha)
                }
                (binding.likeIv.parent as? View)?.let {
                    animateButtonScale(it, likeScale)
                    animateButtonBackground(it, likeAlpha)
                }
            }
            else -> {
                resetButtonState()
            }
        }
    }

    private fun animateButtonScale(button: View, scale: Float) {
        button.scaleX = scale
        button.scaleY = scale
    }

    private fun animateButtonBackground(button: View, alpha: Float) {
        val background = button.background
        if (background is android.graphics.drawable.GradientDrawable) {
            // 更新背景透明度，黑色背景 #000000，alpha范围0-255
            val alphaValue = (alpha * 255).toInt().coerceIn(0, 255)
            val color = (alphaValue shl 24) or 0x000000  // ARGB格式，黑色背景
            background.setColor(color)
            button.invalidate()  // 强制重绘
        } else {
            // 如果不是GradientDrawable，尝试创建新的背景
            val alphaValue = (alpha * 255).toInt().coerceIn(0, 255)
            val color = (alphaValue shl 24) or 0x000000
            val newBackground = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(color)
            }
            button.background = newBackground
        }
    }

    private fun resetButtonState() {
        (binding.likeIv.parent as? View)?.let {
            animateButtonScale(it, defaultScale)
            animateButtonBackground(it, defaultAlpha)
        }
        (binding.unLikeIv.parent as? View)?.let {
            animateButtonScale(it, defaultScale)
            animateButtonBackground(it, defaultAlpha)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


