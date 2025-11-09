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
            switchTab(
                fragment = homeFragment,
                selectedIcon = viewBind.recommendBtn,
                selectedText = viewBind.recommendTv,
                selectedIconRes = R.mipmap.recommend_selecte_icon,
                unselectedIcons = listOf(
                    viewBind.tabBtn2 to R.mipmap.square_normal_icon,
                    viewBind.tabBtn3 to R.mipmap.message,
                    viewBind.tabBtn4 to R.mipmap.me
                ),
                unselectedTexts = listOf(
                    viewBind.tabTv2,
                    viewBind.tabTv3,
                    viewBind.tabTv4
                )
            )
        }
        viewBind.findRl.click {
            switchTab(
                fragment = squareFragment,
                selectedIcon = viewBind.tabBtn2,
                selectedText = viewBind.tabTv2,
                selectedIconRes = R.mipmap.square_selecte_icon,
                unselectedIcons = listOf(
                    viewBind.recommendBtn to R.mipmap.recommend_normal_icon,
                    viewBind.tabBtn3 to R.mipmap.message,
                    viewBind.tabBtn4 to R.mipmap.me
                ),
                unselectedTexts = listOf(
                    viewBind.recommendTv,
                    viewBind.tabTv3,
                    viewBind.tabTv4
                )
            )
        }

        viewBind.messageRl.click {
            switchTab(
                fragment = messageFragment,
                selectedIcon = viewBind.tabBtn3,
                selectedText = viewBind.tabTv3,
                selectedIconRes = R.mipmap.message_in,
                unselectedIcons = listOf(
                    viewBind.recommendBtn to R.mipmap.recommend_normal_icon,
                    viewBind.tabBtn2 to R.mipmap.square_normal_icon,
                    viewBind.tabBtn4 to R.mipmap.me
                ),
                unselectedTexts = listOf(
                    viewBind.recommendTv,
                    viewBind.tabTv2,
                    viewBind.tabTv4
                )
            )
        }

        viewBind.mineRl.click {
            switchTab(
                fragment = profileFragment,
                selectedIcon = viewBind.tabBtn4,
                selectedText = viewBind.tabTv4,
                selectedIconRes = R.mipmap.me_in,
                unselectedIcons = listOf(
                    viewBind.recommendBtn to R.mipmap.recommend_normal_icon,
                    viewBind.tabBtn2 to R.mipmap.square_normal_icon,
                    viewBind.tabBtn3 to R.mipmap.message
                ),
                unselectedTexts = listOf(
                    viewBind.recommendTv,
                    viewBind.tabTv2,
                    viewBind.tabTv3
                )
            )
        }

    }
    
    private fun switchTab(
        fragment: Fragment,
        selectedIcon: android.widget.ImageView,
        selectedText: android.widget.TextView,
        selectedIconRes: Int,
        unselectedIcons: List<Pair<android.widget.ImageView, Int>>,
        unselectedTexts: List<android.widget.TextView>
    ) {
        // 如果点击的是当前已选中的tab，不执行动画
        val isAlreadySelected = activeFragment == fragment
        
        // 切换Fragment
        switchFragment(fragment)
        
        // 更新图标
        selectedIcon.setImageResource(selectedIconRes)
        unselectedIcons.forEach { (icon, res) ->
            icon.setImageResource(res)
        }
        
        // 更新文字颜色（无动画，立即切换）
        selectedText.setTextColor(Color.parseColor("#FF6B6B"))
        unselectedTexts.forEach { textView ->
            textView.setTextColor(Color.parseColor("#2C3E50"))
        }
        
        // 为选中的tab添加动画效果（只有切换到新tab时才执行）
        if (!isAlreadySelected) {
            animateTabSelection(selectedIcon, selectedText)
        }
    }
    
    private fun animateTabSelection(icon: android.widget.ImageView, textView: android.widget.TextView) {
        // 图标缩放动画：放大再恢复，带弹跳效果
        val iconAnimator = android.animation.AnimatorSet()
        
        val scaleUp = android.animation.ObjectAnimator.ofFloat(icon, "scaleX", 1f, 1.3f).apply {
            duration = 200
            interpolator = android.view.animation.DecelerateInterpolator()
        }
        val scaleUpY = android.animation.ObjectAnimator.ofFloat(icon, "scaleY", 1f, 1.3f).apply {
            duration = 200
            interpolator = android.view.animation.DecelerateInterpolator()
        }
        
        val scaleDown = android.animation.ObjectAnimator.ofFloat(icon, "scaleX", 1.3f, 0.9f).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateInterpolator()
        }
        val scaleDownY = android.animation.ObjectAnimator.ofFloat(icon, "scaleY", 1.3f, 0.9f).apply {
            duration = 150
            interpolator = android.view.animation.AccelerateInterpolator()
        }
        
        val scaleBack = android.animation.ObjectAnimator.ofFloat(icon, "scaleX", 0.9f, 1f).apply {
            duration = 150
            interpolator = android.view.animation.OvershootInterpolator(2f)
        }
        val scaleBackY = android.animation.ObjectAnimator.ofFloat(icon, "scaleY", 0.9f, 1f).apply {
            duration = 150
            interpolator = android.view.animation.OvershootInterpolator(2f)
        }
        
        iconAnimator.playTogether(scaleUp, scaleUpY)
        iconAnimator.play(scaleDown).after(scaleUp)
        iconAnimator.play(scaleDownY).after(scaleUpY)
        iconAnimator.play(scaleBack).after(scaleDown)
        iconAnimator.play(scaleBackY).after(scaleDownY)
        
        // 文字向上移动并恢复的动画
        val textAnimator = android.animation.AnimatorSet()
        
        val translateUp = android.animation.ObjectAnimator.ofFloat(textView, "translationY", 0f, -8f).apply {
            duration = 200
            interpolator = android.view.animation.DecelerateInterpolator()
        }
        
        val translateDown = android.animation.ObjectAnimator.ofFloat(textView, "translationY", -8f, 0f).apply {
            duration = 200
            interpolator = android.view.animation.OvershootInterpolator(1.5f)
        }
        
        textAnimator.play(translateUp).before(translateDown)
        
        // 同时执行图标和文字动画
        val animatorSet = android.animation.AnimatorSet()
        animatorSet.playTogether(iconAnimator, textAnimator)
        animatorSet.start()
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