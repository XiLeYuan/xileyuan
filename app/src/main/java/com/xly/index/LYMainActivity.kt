package com.xly.index

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.xly.R
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xly.base.LYBaseActivity
import com.xly.business.favorite.view.FavoriteFragment
import com.xly.business.find.view.FindFragment
import com.xly.business.message.view.MessageListFragment
import com.xly.business.recommend.view.RecommendFragment
import com.xly.business.user.view.ProfileFragment
import com.xly.databinding.ActivityMainBinding
import com.xly.index.viewmodel.MainViewModel
import com.xly.middlelibrary.utils.click

class LYMainActivity: LYBaseActivity<ActivityMainBinding,MainViewModel>() {

    private val homeFragment = RecommendFragment()
//    private val favoriteFragment = FavoriteFragment()
    private val findFragment = FindFragment()
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
            add(R.id.fragmentContainer, findFragment, "2").hide(findFragment)
//            add(R.id.fragmentContainer, favoriteFragment, "2").hide(favoriteFragment)
            add(R.id.fragmentContainer, homeFragment, "1")
        }.commit()

        /*viewBind.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_home -> switchFragment(homeFragment)
                R.id.tab_discover -> switchFragment(favoriteFragment)
                R.id.tab_offline -> switchFragment(findFragment)
                R.id.tab_message -> switchFragment(messageFragment)
                R.id.tab_profile -> switchFragment(profileFragment)
            }
            true
        }*/
    }

    override fun initOnClick() {
        viewBind.recommendBtn.click {
            switchFragment(homeFragment)
        }
        viewBind.findRl.click {
            switchFragment(findFragment)
        }

        viewBind.messageRl.click {
            switchFragment(messageFragment)
        }

        viewBind.mineRl.click {
            switchFragment(profileFragment)
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