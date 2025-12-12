package com.xly.business.vip.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.xly.base.LYBaseActivity
import com.xly.business.vip.model.VipRechargeOption
import com.xly.databinding.ActivityVipRechargeBinding
import com.xly.index.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class LookStarMeActivity : LYBaseActivity<ActivityVipRechargeBinding, MainViewModel>() {

    companion object {
        fun start(c: Context) {
            val intent = Intent(c, LookStarMeActivity::class.java)
            (c as? Activity)?.startActivity(intent)
        }
    }

    private var selectedOption: VipRechargeOption? = null

    // 订阅选项数据
    private val subscriptionOptions = listOf(
        SubscriptionOption(
            discountTag = "专属2.9折",
            packageType = "连续包年",
            duration = "12个月",
            price = "¥688",
            monthlyPrice = "¥57.3/月",
            isRecommended = true,
            option = VipRechargeOption("连续包年", "¥798", "¥688", "¥57.3/月", isRecommended = true, isContinuous = true)
        ),
        SubscriptionOption(
            discountTag = "专属5.4折",
            packageType = "连续包季",
            duration = "3个月",
            price = "¥318",
            monthlyPrice = "¥106/月",
            isRecommended = false,
            option = VipRechargeOption("连续包季", null, "¥318", "¥106/月", isRecommended = false, isContinuous = true)
        ),
        SubscriptionOption(
            discountTag = "尝新首选",
            packageType = "连续包月",
            duration = "1个月",
            price = "¥198",
            monthlyPrice = "¥198/月",
            isRecommended = false,
            option = VipRechargeOption("连续包月", null, "¥198", "¥198/月", isRecommended = false, isContinuous = true)
        ),
        SubscriptionOption(
            discountTag = "专属2.5折",
            packageType = "年卡",
            duration = "12个月",
            price = "¥798",
            monthlyPrice = "¥66.5/月",
            isRecommended = false,
            option = VipRechargeOption("年卡", null, "¥798", "¥66.5/月", isRecommended = false, isContinuous = false)
        ),
        SubscriptionOption(
            discountTag = "专属5.2折",
            packageType = "季卡",
            duration = "3个月",
            price = "¥418",
            monthlyPrice = "¥139.3/月",
            isRecommended = false,
            option = VipRechargeOption("季卡", null, "¥418", "¥139.3/月", isRecommended = false, isContinuous = false)
        ),
        SubscriptionOption(
            discountTag = "尝新首选",
            packageType = "月卡",
            duration = "1个月",
            price = "¥268",
            monthlyPrice = "¥268/月",
            isRecommended = false,
            option = VipRechargeOption("月卡", null, "¥268", "¥268/月", isRecommended = false, isContinuous = false)
        )
    )

    // 特权数据
    private val privileges = listOf(
        PrivilegeItem("解锁喜欢", "免费看全部", "100币/位"),
        PrivilegeItem("解锁访客", "免费看12位", "100币/位"),
        PrivilegeItem("小纸条", "每天免费1个", "100币/个"),
        PrivilegeItem("无限滑卡", "无限制", "每日限10次"),
        PrivilegeItem("位置漫游", "任意切换", "不可用"),
        PrivilegeItem("主动打招呼", "每天5次", "不可用"),
        PrivilegeItem("查看谁喜欢我", "免费查看", "需付费"),
        PrivilegeItem("优先推荐", "优先展示", "普通推荐"),
        PrivilegeItem("专属标识", "VIP标识", "无标识")
    )

    data class SubscriptionOption(
        val discountTag: String,
        val packageType: String,
        val duration: String,
        val price: String,
        val monthlyPrice: String,
        val isRecommended: Boolean,
        val option: VipRechargeOption
    )

    data class PrivilegeItem(
        val benefit: String,
        val vipBenefit: String,
        val regularBenefit: String
    )

    override fun initView() {
        setupDateRange()
        setupUserInfo()
        setupSubscriptionOptions()
        setupPrivilegesTable()
        setupDefaultSelection()
        setupScrollListener()
    }
    
    private fun setupScrollListener() {
        viewBind.nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = viewBind.nestedScrollView.scrollY
            updateToolbarBackground(scrollY)
        }
    }
    
    private fun updateToolbarBackground(scrollY: Int) {
        val toolbarContainer = viewBind.toolbarContainer
        val startThreshold = dp2px(0) // 开始渐变的位置
        val endThreshold = dp2px(100) // 完全变成白色的位置
        
        // 计算渐变系数 (0.0 - 1.0)
        val ratio = when {
            scrollY <= startThreshold -> 0f // 完全透明
            scrollY >= endThreshold -> 1f // 完全白色
            else -> {
                // 在阈值之间，计算渐变比例
                (scrollY - startThreshold).toFloat() / (endThreshold - startThreshold)
            }
        }
        
        // 使用渐变系数混合透明和白色
        // 白色: #FFFFFFFF, 透明: #00FFFFFF
        val alpha = (ratio * 255).toInt().coerceIn(0, 255)
        val color = Color.argb(alpha, 255, 255, 255)
        toolbarContainer.setBackgroundColor(color)
    }

    override fun initOnClick() {
        viewBind.ivBack.setOnClickListener { finish() }
        viewBind.tvRules.setOnClickListener {
            // TODO: 打开规则页面
            showToast("规则")
        }
        viewBind.btnSubscribe.setOnClickListener {
            if (!viewBind.cbAgreement.isChecked()) {
                showToast("请先同意协议")
                return@setOnClickListener
            }
            if (selectedOption == null) {
                showToast("请选择订阅套餐")
                return@setOnClickListener
            }
            // TODO: 调用支付
            showToast("开通会员: ${selectedOption?.currentPrice}")
        }
        viewBind.tvAgreementText.setOnClickListener {
            // TODO: 打开协议页面
        }
    }

    private fun setupDateRange() {
        // 显示当前月份的9号到14号
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        
        val startDate = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 9)
        }
        val endDate = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 14)
        }
        
        val dateFormat = SimpleDateFormat("M.d", Locale.getDefault())
        val dateRange = "${dateFormat.format(startDate.time)}-${dateFormat.format(endDate.time)}"
    }

    private fun setupUserInfo() {
        // TODO: 从用户信息中获取真实数据
        viewBind.tvUserStatus.text = "青藤用户6942 未开通会员"
        Glide.with(this)
            .load(com.xly.R.mipmap.head_img)
            .circleCrop()
            .into(viewBind.ivUserAvatar)
    }

    private fun setupSubscriptionOptions() {
        val container = viewBind.llSubscriptionOptions
        container.removeAllViews()

        subscriptionOptions.forEach { option ->
            val cardView = LayoutInflater.from(this)
                .inflate(com.xly.R.layout.item_vip_subscription_card, container, false)

            val tvDiscountTag = cardView.findViewById<TextView>(com.xly.R.id.tvDiscountTag)
            val tvPackageType = cardView.findViewById<TextView>(com.xly.R.id.tvPackageType)
            val tvDuration = cardView.findViewById<TextView>(com.xly.R.id.tvDuration)
            val tvPrice = cardView.findViewById<TextView>(com.xly.R.id.tvPrice)
            val tvMonthlyPrice = cardView.findViewById<TextView>(com.xly.R.id.tvMonthlyPrice)
            val layoutRoot = cardView.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
            val cardRoot = cardView.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)
            
            // 设置标签文本
            tvDiscountTag.text = option.discountTag

            tvPackageType.text = option.packageType
            tvDuration.text = option.duration
            tvPrice.text = option.price
            tvMonthlyPrice.text = option.monthlyPrice

            // 设置初始宽度和选中状态样式
            val normalWidth = dp2px(100)
            val selectedWidth = dp2px(115)
            
            if (option.isRecommended) {
                layoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border_primary)
                cardRoot.layoutParams.width = selectedWidth
                cardRoot.requestLayout()
            } else {
                layoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border)
                cardRoot.layoutParams.width = normalWidth
                cardRoot.requestLayout()
            }

            // 点击选择
            cardView.setOnClickListener {
                selectedOption = option.option
                updateSubscriptionSelection(cardView)
            }

            container.addView(cardView)
        }
    }

    private fun updateSubscriptionSelection(selectedView: View) {
        val container = viewBind.llSubscriptionOptions
        val normalWidth = dp2px(100)
        val selectedWidth = dp2px(115)
        
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val layoutRoot = child.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
            val cardRoot = child.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)
            
            if (layoutRoot != null && cardRoot != null) {
                if (child == selectedView) {
                    // 选中状态：橙色边框，宽度变大
                    layoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border_primary)
                    animateWidth(cardRoot, cardRoot.layoutParams.width, selectedWidth)
                } else {
                    // 未选中状态：灰色边框，宽度变小
                    layoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border)
                    animateWidth(cardRoot, cardRoot.layoutParams.width, normalWidth)
                }
            }
        }
    }
    
    private fun animateWidth(view: View, fromWidth: Int, toWidth: Int) {
        if (fromWidth == toWidth) return
        
        val animator = android.animation.ValueAnimator.ofInt(fromWidth, toWidth)
        animator.duration = 300
        animator.interpolator = android.view.animation.DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            val width = animation.animatedValue as Int
            view.layoutParams.width = width
            view.requestLayout()
        }
        animator.start()
    }



    private fun setupPrivilegesTable() {
        val container = viewBind.llPrivilegesTable
        // 移除表头后的所有子视图（保留表头和分割线）
        val childCount = container.childCount
        for (i in childCount - 1 downTo 2) {
            container.removeViewAt(i)
        }

        privileges.forEach { privilege ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 0)
                }
            }

            // 权益名称
            val tvBenefit = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
                text = privilege.benefit
                setTextColor(Color.parseColor("#FF333333"))
                textSize = 13f
                setPadding(0, dp2px(12), 0, dp2px(12))
            }

            // VIP会员用户权益
            val tvVipBenefit = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                gravity = android.view.Gravity.CENTER
                text = privilege.vipBenefit
                setTextColor(Color.parseColor("#FF333333"))
                textSize = 13f
                setBackgroundColor(Color.parseColor("#FFF9E6"))
                setPadding(0, dp2px(12), 0, dp2px(12))
            }

            // 普通用户权益
            val tvRegularBenefit = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                gravity = android.view.Gravity.CENTER
                text = privilege.regularBenefit
                setTextColor(Color.parseColor("#FF999999"))
                textSize = 13f
                setPadding(0, dp2px(12), 0, dp2px(12))
            }

            rowLayout.addView(tvBenefit)
            rowLayout.addView(tvVipBenefit)
            rowLayout.addView(tvRegularBenefit)
            container.addView(rowLayout)
        }
    }

    private fun setupDefaultSelection() {
        selectedOption = subscriptionOptions.first().option
        if (viewBind.llSubscriptionOptions.childCount > 0) {
            val firstCard = viewBind.llSubscriptionOptions.getChildAt(0)
            // 直接设置初始状态，不使用动画
            val layoutRoot = firstCard.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
            val cardRoot = firstCard.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)
            if (layoutRoot != null && cardRoot != null) {
                layoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border_primary)
                cardRoot.layoutParams.width = dp2px(115)
                cardRoot.requestLayout()
            }
            
            // 设置其他卡片为未选中状态
            for (i in 1 until viewBind.llSubscriptionOptions.childCount) {
                val child = viewBind.llSubscriptionOptions.getChildAt(i)
                val childLayoutRoot = child.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
                val childCardRoot = child.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)
                if (childLayoutRoot != null && childCardRoot != null) {
                    childLayoutRoot.setBackgroundResource(com.xly.R.drawable.vip_card_item_border)
                    childCardRoot.layoutParams.width = dp2px(100)
                    childCardRoot.requestLayout()
                }
            }
        }
    }

    private fun dp2px(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityVipRechargeBinding {
        return ActivityVipRechargeBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return androidx.lifecycle.ViewModelProvider(this)[MainViewModel::class.java]
    }
}
