package com.xly.index

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.xly.R

@SuppressLint("CustomSplashScreen")
class SplashActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initImmersionBar()
    }

    private fun initImmersionBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init();
    }

}