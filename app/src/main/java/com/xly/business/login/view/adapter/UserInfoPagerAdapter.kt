package com.xly.business.login.view.adapter

// FragmentStateAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xly.business.login.view.UserInfoStepFragment

class UserInfoPagerAdapter(
    fa: FragmentActivity,
    private val totalSteps: Int
) : FragmentStateAdapter(fa) {
    private val stepValid = BooleanArray(totalSteps) { false }
    override fun getItemCount() = totalSteps
    override fun createFragment(position: Int): Fragment {
        return UserInfoStepFragment.newInstance(position)
    }
    fun setStepValid(step: Int, valid: Boolean) {
        stepValid[step] = valid
    }
    fun isStepValid(step: Int): Boolean = stepValid[step]
}