package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xly.R
import com.xly.base.LYBaseActivity
import com.xly.business.login.view.LoginActivity
import com.xly.databinding.ActivitySettingsBinding
import com.xly.middlelibrary.utils.click

class SettingsActivity : LYBaseActivity<ActivitySettingsBinding, SettingsViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): SettingsViewModel {
        return ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun initView() {
        setupSettingItems()
        setupVersionInfo()
    }

    override fun initOnClick() {
        // 返回按钮
        viewBind.ivBack.click {
            finish()
        }

        // 退出登录按钮
        viewBind.btnLogout.click {
            handleLogout()
        }
    }

    private fun setupSettingItems() {
        // 通用
        val itemGeneral = findViewById<View>(R.id.itemGeneral)
        val tvGeneral = itemGeneral.findViewById<TextView>(R.id.tvTitle)
        tvGeneral.text = "通用"
        itemGeneral.setOnClickListener {
            showToast("通用设置")
        }

        // 账号与安全
        val itemAccountSecurity = findViewById<View>(R.id.itemAccountSecurity)
        val tvAccountSecurity = itemAccountSecurity.findViewById<TextView>(R.id.tvTitle)
        tvAccountSecurity.text = "账号与安全"
        itemAccountSecurity.setOnClickListener {
            showToast("账号与安全")
        }

        // 隐私管理
        val itemPrivacy = findViewById<View>(R.id.itemPrivacy)
        val tvPrivacy = itemPrivacy.findViewById<TextView>(R.id.tvTitle)
        tvPrivacy.text = "隐私管理"
        itemPrivacy.setOnClickListener {
            showToast("隐私管理")
        }

        // 自动续费管理
        val itemAutoRenewal = findViewById<View>(R.id.itemAutoRenewal)
        val tvAutoRenewal = itemAutoRenewal.findViewById<TextView>(R.id.tvTitle)
        tvAutoRenewal.text = "自动续费管理"
        itemAutoRenewal.setOnClickListener {
            showToast("自动续费管理")
        }

        // 数据与缓存
        val itemDataCache = findViewById<View>(R.id.itemDataCache)
        val tvDataCache = itemDataCache.findViewById<TextView>(R.id.tvTitle)
        tvDataCache.text = "数据与缓存"
        itemDataCache.setOnClickListener {
            showToast("数据与缓存")
        }

        // 帮助与反馈
        val itemHelpFeedback = findViewById<View>(R.id.itemHelpFeedback)
        val tvHelpFeedback = itemHelpFeedback.findViewById<TextView>(R.id.tvTitle)
        tvHelpFeedback.text = "帮助与反馈"
        itemHelpFeedback.setOnClickListener {
            showToast("帮助与反馈")
        }

        // 关于结婚吧
        val itemAbout = findViewById<View>(R.id.itemAbout)
        val tvAbout = itemAbout.findViewById<TextView>(R.id.tvTitle)
        tvAbout.text = "关于结婚吧"
        itemAbout.setOnClickListener {
            showToast("关于结婚吧")
        }

        // 给结婚吧好评
        val itemRate = findViewById<View>(R.id.itemRate)
        val tvRate = itemRate.findViewById<TextView>(R.id.tvTitle)
        tvRate.text = "给结婚吧好评"
        itemRate.setOnClickListener {
            showToast("给结婚吧好评")
        }
    }

    private fun setupVersionInfo() {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName ?: "1.0.0"
            viewBind.tvVersion.text = "版本号：$versionName"
        } catch (e: PackageManager.NameNotFoundException) {
            viewBind.tvVersion.text = "版本号：1.0.0"
        }
    }

    private fun handleLogout() {
        // TODO: 实现退出登录逻辑
//        showToast("退出登录")
        // 示例：退出登录后跳转到登录页
         LoginActivity.start(this)
         finish()
    }
}

class SettingsViewModel : ViewModel() {
    // 可以在这里添加设置相关的数据逻辑
}

