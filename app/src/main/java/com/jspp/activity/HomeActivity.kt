package com.jspp.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.yuyakaido.android.cardstackview.*
import com.xly.R
import com.jspp.model.UserCard
import com.jspp.adapter.UserCardAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var cardStackView: CardStackView
    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var userCardAdapter: UserCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupCardStackView()
        loadCardData()
    }

    private fun initViews() {
        cardStackView = findViewById(R.id.cardStackView)

        // 设置底部按钮点击事件
        findViewById<View>(R.id.btnDislike).setOnClickListener {
            cardStackView.swipe()
        }

        findViewById<View>(R.id.btnLike).setOnClickListener {
            cardStackView.swipe()
        }

        findViewById<View>(R.id.btnDetail).setOnClickListener {
            val currentPosition = cardStackLayoutManager.topPosition
            if (currentPosition < userCardAdapter.getItemCount()) {
                val userCard = userCardAdapter.getUserCard(currentPosition)
                userCard?.let {
                    showUserDetail(it)
                }

            }
        }
    }

    private fun setupCardStackView() {
        // 创建布局管理器
        cardStackLayoutManager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {
                // 卡片拖动中
            }

            override fun onCardSwiped(direction: Direction) {
                // 卡片滑动完成
                when (direction) {
                    Direction.Left -> {
                        Toast.makeText(this@HomeActivity, "不喜欢", Toast.LENGTH_SHORT).show()
                    }
                    Direction.Right -> {
                        Toast.makeText(this@HomeActivity, "喜欢", Toast.LENGTH_SHORT).show()
                    }
                    Direction.Top -> {
                        Toast.makeText(this@HomeActivity, "超级喜欢", Toast.LENGTH_SHORT).show()
                    }
                    Direction.Bottom -> {
                        Toast.makeText(this@HomeActivity, "跳过", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCardRewound() {
                // 卡片回退
                Toast.makeText(this@HomeActivity, "撤销操作", Toast.LENGTH_SHORT).show()
            }

            override fun onCardCanceled() {
                // 卡片取消滑动
            }

            override fun onCardAppeared(view: View, position: Int) {
                // 卡片出现
            }

            override fun onCardDisappeared(view: View, position: Int) {
                // 卡片消失
            }
        }).apply {
            // 设置堆叠方向
            setStackFrom(StackFrom.None)

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
            setMaxDegree(20f)

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
        cardStackView.layoutManager = cardStackLayoutManager
        cardStackView.adapter = userCardAdapter
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
                tags = listOf("旅行", "摄影", "音乐")
            ),
            UserCard(
                id = "2",
                name = "小雨",
                age = 23,
                location = "上海",
                avatarUrl = "https://example.com/avatar2.jpg",
                bio = "热爱生活，喜欢尝试新事物，希望能遇到有趣的人。",
                tags = listOf("美食", "运动", "阅读")
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

    /**
     * 显示用户详情页
     */
    private fun showUserDetail(userCard: UserCard, cardView: View? = null) {
        val intent = Intent(this, UserDetailActivity::class.java).apply {
            putExtra("user_card", userCard)
        }

        if (cardView != null) {
            // 使用共享元素转场动画
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                cardView,
                "user_card"
            )
            startActivity(intent, options.toBundle())
        } else {
            // 普通转场
            startActivity(intent)
        }
    }

    /**
     * 撤销上一次滑动
     */
    fun rewind() {
        cardStackView.rewind()
    }

    /**
     * 获取当前卡片位置
     */
    fun getCurrentPosition(): Int {
        return cardStackLayoutManager.topPosition
    }

    /**
     * 获取剩余卡片数量
     */
    fun getRemainingCount(): Int {
        return userCardAdapter.getItemCount() - cardStackLayoutManager.topPosition
    }

    /**
     * 检查是否还有卡片
     */
    fun hasMoreCards(): Boolean {
        return cardStackLayoutManager.topPosition < userCardAdapter.getItemCount()
    }

    /**
     * 重新加载卡片
     */
    fun reloadCards() {
        loadCardData()
    }
}