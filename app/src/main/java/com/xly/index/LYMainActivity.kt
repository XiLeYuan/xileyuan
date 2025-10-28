package com.xly.index

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.xly.R
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.message.view.MessageListFragment
import com.xly.business.recommend.view.RecommendFragment
import com.xly.business.square.SquareFragment
import com.xly.business.user.view.ProfileFragment
import com.xly.databinding.ActivityMainBinding
import com.xly.index.viewmodel.MainViewModel
import com.xly.middlelibrary.utils.click

class LYMainActivity: LYBaseActivity<ActivityMainBinding,MainViewModel>() {

    private val homeFragment = RecommendFragment()
    private val squareFragment = SquareFragment()
    private val messageFragment = MessageListFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    companion object {

        fun start(c: Context) {
            val intent = Intent(c, LYMainActivity::class.java)
            (c as? Activity)?.startActivity(intent)
            (c as? Activity)?.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, profileFragment, "4").hide(profileFragment)
            add(R.id.fragmentContainer, messageFragment, "3").hide(messageFragment)
            add(R.id.fragmentContainer, squareFragment, "2").hide(squareFragment)
            add(R.id.fragmentContainer, homeFragment, "1")
        }.commit()

        viewBind.recommendBtn.setImageResource(R.mipmap.recommend_selecte_icon)
        viewBind.tabBtn2.setImageResource(R.mipmap.square_normal_icon)
        viewBind.tabBtn3.setImageResource(R.mipmap.message)
        viewBind.tabBtn4.setImageResource(R.mipmap.me)

        viewBind.recommendTv.setTextColor(Color.parseColor("#FF6B6B"))
        viewBind.tabTv2.setTextColor(Color.parseColor("#2C3E50"))
        viewBind.tabTv3.setTextColor(Color.parseColor("#2C3E50"))
        viewBind.tabTv4.setTextColor(Color.parseColor("#2C3E50"))

    }

    override fun initOnClick() {
        viewBind.recommendRl.click {
            switchFragment(homeFragment)
            viewBind.recommendBtn.setImageResource(R.mipmap.recommend_selecte_icon)
            viewBind.tabBtn2.setImageResource(R.mipmap.square_normal_icon)
            viewBind.tabBtn3.setImageResource(R.mipmap.message)
            viewBind.tabBtn4.setImageResource(R.mipmap.me)
            viewBind.recommendTv.setTextColor(Color.parseColor("#FF6B6B"))
            viewBind.tabTv2.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv3.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv4.setTextColor(Color.parseColor("#2C3E50"))
        }
        viewBind.findRl.click {
            switchFragment(squareFragment)
            viewBind.recommendBtn.setImageResource(R.mipmap.recommend_normal_icon)
            viewBind.tabBtn2.setImageResource(R.mipmap.square_selecte_icon)
            viewBind.tabBtn3.setImageResource(R.mipmap.message)
            viewBind.tabBtn4.setImageResource(R.mipmap.me)
            viewBind.recommendTv.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv2.setTextColor(Color.parseColor("#FF6B6B"))
            viewBind.tabTv3.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv4.setTextColor(Color.parseColor("#2C3E50"))
        }

        viewBind.messageRl.click {
            switchFragment(messageFragment)
            viewBind.tabBtn3.setImageResource(R.mipmap.message_in)
            viewBind.recommendBtn.setImageResource(R.mipmap.recommend_normal_icon)
            viewBind.tabBtn2.setImageResource(R.mipmap.square_normal_icon)
            viewBind.tabBtn4.setImageResource(R.mipmap.me)
            viewBind.recommendTv.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv2.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv3.setTextColor(Color.parseColor("#FF6B6B"))
            viewBind.tabTv4.setTextColor(Color.parseColor("#2C3E50"))
        }

        viewBind.mineRl.click {
            switchFragment(profileFragment)
            viewBind.tabBtn4.setImageResource(R.mipmap.me_in)
            viewBind.tabBtn3.setImageResource(R.mipmap.message)
            viewBind.recommendBtn.setImageResource(R.mipmap.recommend_normal_icon)
            viewBind.tabBtn2.setImageResource(R.mipmap.square_normal_icon)
            viewBind.recommendTv.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv2.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv3.setTextColor(Color.parseColor("#2C3E50"))
            viewBind.tabTv4.setTextColor(Color.parseColor("#FF6B6B"))
        }

    }


    private fun switchFragment(target: Fragment) {
        if (activeFragment != target) {
            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit()
            activeFragment = target
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

}