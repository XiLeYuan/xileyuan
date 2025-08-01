package com.jspp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.xly.R
import com.jspp.model.UserCard

class TestUserDetailActivity : AppCompatActivity() {

    companion object {

        fun start(c: Context) {
            val intent = Intent(c, TestUserDetailActivity::class.java)
            (c as? Activity)?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_user_detail)

        // 测试按钮
        findViewById<Button>(R.id.btnTestDetail).setOnClickListener {
            val testUser = UserCard(
                id = "test_001",
                name = "测试用户",
                age = 25,
                location = "北京",
                avatarUrl = "https://example.com/test.jpg",
                bio = "这是一个测试用户，用于验证滑动悬停效果。",
                tags = listOf("测试", "滑动", "悬停"),
                photos = listOf("https://example.com/photo1.jpg"),
                occupation = "软件工程师",
                education = "本科",
                height = 165,
                weight = 50,
                isOnline = true,
                distance = "1.2km",
                lastActiveTime = System.currentTimeMillis()
            )

            val intent = Intent(this, UserDetailActivity::class.java).apply {
                putExtra("user_card", testUser)
            }
            startActivity(intent)
        }
    }
} 