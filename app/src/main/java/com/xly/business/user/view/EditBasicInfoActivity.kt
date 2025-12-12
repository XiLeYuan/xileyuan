package com.xly.business.user.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.databinding.ActivityEditBasicInfoBinding
import com.xly.middlelibrary.utils.click

class EditBasicInfoActivity : LYBaseActivity<ActivityEditBasicInfoBinding, EditBasicInfoViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditBasicInfoActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityEditBasicInfoBinding {
        return ActivityEditBasicInfoBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): EditBasicInfoViewModel {
        return ViewModelProvider(this)[EditBasicInfoViewModel::class.java]
    }

    override fun initView() {
        // TODO: 初始化编辑基本资料的界面
    }

    override fun initOnClick() {
        // 返回按钮
        viewBind.ivBack.click {
            finish()
        }
    }
}

class EditBasicInfoViewModel : ViewModel() {
    // 可以在这里添加编辑基本资料的数据逻辑
}


