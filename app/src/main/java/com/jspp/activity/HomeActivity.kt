package com.jspp.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xly.R
import com.jspp.model.UserCard
import com.yuyakaido.android.cardstackview.CardStackView
import com.jspp.utils.MMKVManager

class HomeActivity : AppCompatActivity() {

    private lateinit var cardStackView: CardStackView

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
            cardStackView.currentIndex?.let { index ->
                if (index < cardStackView.cards.size) {
                    // 模拟向左滑动
                    simulateSwipe(CardStackView.SwipeDirection.LEFT)
                }
            }
        }

        findViewById<View>(R.id.btnLike).setOnClickListener {
            cardStackView.currentIndex?.let { index ->
                if (index < cardStackView.cards.size) {
                    // 模拟向右滑动
                    simulateSwipe(CardStackView.SwipeDirection.RIGHT)
                }
            }
        }

        findViewById<View>(R.id.btnDetail).setOnClickListener {
            cardStackView.currentIndex?.let { index ->
                if (index < cardStackView.cards.size) {
                    // 查看详情
                    showUserDetail(cardStackView.cards[index])
                }
            }
        }
    }

    private fun setupCardStackView() {
        // 设置卡片点击监听
        cardStackView.setOnCardClickListener { userCard, cardView ->
            showUserDetail(userCard, cardView)
        }

        // 设置卡片滑动监听
        cardStackView.setOnCardSwipeListener { userCard, direction ->
            when (direction) {
                CardStackView.SwipeDirection.LEFT -> {
                    Toast.makeText(this, "不喜欢 ${userCard.name}", Toast.LENGTH_SHORT).show()
                }
                CardStackView.SwipeDirection.RIGHT -> {
                    Toast.makeText(this, "喜欢 ${userCard.name}", Toast.LENGTH_SHORT).show()
                }
            }
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
            )
        )

        cardStackView.setCards(cards)
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
     * 模拟滑动效果
     */
    private fun simulateSwipe(direction: CardStackView.SwipeDirection) {
        // 这里可以添加模拟滑动的逻辑
        // 实际项目中可能需要调用CardStackView的内部方法
        Toast.makeText(this, "模拟${if (direction == CardStackView.SwipeDirection.RIGHT) "右" else "左"}滑", Toast.LENGTH_SHORT).show()
    }
}