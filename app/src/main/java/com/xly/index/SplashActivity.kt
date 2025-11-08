package com.xly.index

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.xly.R
import com.xly.business.login.view.LoginActivity
import com.xly.middlelibrary.utils.MMKVManager
import kotlinx.coroutines.Runnable

@SuppressLint("CustomSplashScreen")
class SplashActivity :AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initImmersionBar()
        enterMain()
    }

    private fun initImmersionBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
    }

    private fun enterMain() {
        handler.postDelayed(Runnable {
            val hasAuth = MMKVManager.getBoolean(MMKVManager.KEY_AUTH_SUCCESS)
            if (hasAuth) {
                LYMainActivity.start(this)
            } else {
                LoginActivity.start(this)
            }
        },2000)
    }

}