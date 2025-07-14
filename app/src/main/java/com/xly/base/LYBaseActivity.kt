package com.xly.base
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.xly.R

abstract class LYBaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    lateinit var viewBind: VB
    lateinit var viewModel: VM



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTabBar()
        viewBind = inflateBinding(layoutInflater)
        setContentView(viewBind.root)  // 设置布局

        viewModel = initViewModel()
        acceptData()
        initObservers()
        initView()
        initOnClick()
    }

    open fun acceptData() {}


    private fun initTabBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            .init()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


    /**初始化viewBind*/
    abstract fun inflateBinding(layoutInflater: LayoutInflater): VB

    /**初始化viewModel*/
    abstract fun initViewModel(): VM


    open fun initObservers() {}

    open fun initView() {}

    open fun refreshView(o: Any ?= null) {}

    open fun initOnClick() {}


    /************************************公共UI提示类***************************************************/


    @SuppressLint("InflateParams")
    fun showToast(str: String) {
        val layout = layoutInflater.inflate(R.layout.view_toast_custom, null)
        val tvToast: TextView = layout.findViewById(R.id.toast_text)
        tvToast.setText(str)
        val toast = Toast(this)
        toast.setDuration(Toast.LENGTH_SHORT)
        toast.setView(layout)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showLoading() {
        if (!isFinishing && !isDestroyed) {

        }
    }

    fun cancelLoading() {
        if (!isFinishing && !isDestroyed) {

        }
    }






}