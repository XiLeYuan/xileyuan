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
import com.xly.business.login.view.PrivacyAgreementDialog
import com.xly.business.login.view.UserInfoActivity
import com.xly.business.login.view.UserInfoFirstStepActivity
import com.xly.middlelibrary.utils.MMKVManager
import kotlinx.coroutines.Runnable

@SuppressLint("CustomSplashScreen")
class SplashActivity :AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initImmersionBar()
        checkPrivacyAgreement()
    }

    private fun initImmersionBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
    }

    private fun checkPrivacyAgreement() {
        val hasAgreed = MMKVManager.getBoolean(MMKVManager.KEY_PRIVACY_AGREED)
        if (hasAgreed) {
            // 已同意，延迟后进入主流程
            enterMain()
        } else {
            // 未同意，显示隐私协议弹窗
            handler.postDelayed(Runnable {
                showPrivacyDialog()
            }, 1500)
        }
    }

    private fun showPrivacyDialog() {
        val dialog = PrivacyAgreementDialog(this)
        dialog.onAgreeClick = {
            // 同意后进入主流程
            enterMain()
        }
        dialog.onDisagreeClick = {
            // 不同意，退出应用
            finish()
        }
        dialog.show()
    }

    private fun enterMain() {
        handler.postDelayed(Runnable {
            val hasAuth = MMKVManager.getBoolean(MMKVManager.KEY_AUTH_SUCCESS)
            if (hasAuth) {
//                LYMainActivity.start(this)
                UserInfoFirstStepActivity.start(this)
            } else {
                LoginActivity.start(this)
            }
        }, 500)
    }

}