package com.xly.business.user.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xly.base.LYBaseActivity
import com.xly.databinding.ActivityWalletBinding
import com.xly.index.viewmodel.MainViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 钱包页面
 * 显示钻石币余额、充值选项、用途说明等
 */
class WalletActivity : LYBaseActivity<ActivityWalletBinding, MainViewModel>() {

    companion object {
        fun start(c: Context) {
            val intent = Intent(c, WalletActivity::class.java)
            (c as? Activity)?.startActivity(intent)
        }
    }

    // 充值选项数据模型
    data class RechargeOption(
        val tag: String,              // 标签文字，如"低价特惠"、"尝新首选"、"最多人选"、"节省"
        val coinAmount: Int,          // 钻石币数量
        val extraInfo: String? = null, // 额外说明，如"=100位嘉宾"
        val price: String,           // 价格，如"¥56"
        val discountTag: String? = null,  // 折扣标签，如"8.3折"
        val isRecommended: Boolean = false  // 是否推荐（显示橙色边框）
    )

    // 用途数据模型
    data class UsageItem(
        val iconRes: Int,
        val text: String
    )

    private val rechargeOptions = listOf(
        RechargeOption("低价特惠", 500, null, "¥56", null, false),
        RechargeOption("尝新首选", 1000, "=100位嘉宾", "¥108", null, false),
        RechargeOption("最多人选", 3000, null, "¥268", "8.3折", true),
        RechargeOption("节省", 6000, null, "¥408", "6.8折", false)
    )

    private val usageItems = listOf(
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "送小纸条"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "揭秘喜欢"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "解锁访客"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "提升曝光"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "解锁精选"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "更多推荐"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "匿名解锁"),
        UsageItem(com.xly.R.mipmap.yuan_bi_icon, "限定活动")
    )

    private var selectedOption: RechargeOption? = null

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityWalletBinding {
        return ActivityWalletBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun initView() {
        super.initView()
        
        // 设置钻石币余额（这里可以从服务器获取）
        viewBind.tvDiamondBalance.text = "0"
        
        // 设置充值选项
        setupRechargeOptions()
        
        // 设置用途网格
        setupUsageGrid()
        
        // 默认选中第三个（最多人选）
        selectedOption = rechargeOptions[2]
        // 延迟更新选中状态，确保视图已渲染
        viewBind.llRechargeOptions.post {
            val selectedCard = viewBind.llRechargeOptions.getChildAt(2)
            if (selectedCard != null) {
                updateRechargeSelection(selectedCard)
            }
        }
    }

    override fun initOnClick() {
        super.initOnClick()
        
        // 返回按钮
        viewBind.ivBack.setOnClickListener {
            finish()
        }
        
        // 帮助按钮
        viewBind.ivHelp.setOnClickListener {
            // TODO: 显示帮助信息
            showToast("帮助功能开发中")
        }
        
        // 查看明细按钮
        viewBind.tvViewDetails.setOnClickListener {
            // TODO: 跳转到明细页面
            showToast("明细功能开发中")
        }
        
        // 立即购买按钮
        viewBind.btnBuyNow.setOnClickListener {
            // 检查是否同意协议
            if (!viewBind.cbAgreement.isChecked()) {
                showToast("请先同意《钻石币充值服务协议》")
                return@setOnClickListener
            }
            
            selectedOption?.let { option ->
                // TODO: 调用支付接口
                showToast("购买 ${option.coinAmount} 钻石币，价格 ${option.price}")
            }
        }
        
        // 恢复购买按钮
        viewBind.tvRestorePurchase.setOnClickListener {
            // TODO: 恢复购买逻辑
            showToast("恢复购买功能开发中")
        }
        
        // 协议文本点击
        viewBind.tvAgreement.setOnClickListener {
            // TODO: 打开协议页面
            showToast("协议功能开发中")
        }
        
        // 协议复选框选中状态变化监听
        viewBind.cbAgreement.onCheckedChangeListener = { checkbox, isChecked ->
            // 可以在这里处理选中状态变化
        }
    }

    /**
     * 设置充值选项卡片
     */
    private fun setupRechargeOptions() {
        val container = viewBind.llRechargeOptions
        container.removeAllViews()

        rechargeOptions.forEachIndexed { index, option ->
            val cardView = LayoutInflater.from(this)
                .inflate(com.xly.R.layout.item_wallet_recharge_card, container, false)

            val tvTag = cardView.findViewById<TextView>(com.xly.R.id.tvTag)
            val tvCoinAmount = cardView.findViewById<TextView>(com.xly.R.id.tvCoinAmount)
            val tvExtraInfo = cardView.findViewById<TextView>(com.xly.R.id.tvExtraInfo)
            val tvPrice = cardView.findViewById<TextView>(com.xly.R.id.tvPrice)
            val tvDiscountTag = cardView.findViewById<TextView>(com.xly.R.id.tvDiscountTag)
            val layoutRoot = cardView.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
            val cardRoot = cardView.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)

            // 设置标签（统一使用橙色背景，白色文字，与VIP一致）
            tvTag.text = option.tag
            tvTag.setBackgroundResource(com.xly.R.drawable.bg_discount_tag_rounded)
            tvTag.setTextColor(0xFFFFFFFF.toInt())
            
            // 设置币数量
            tvCoinAmount.text = option.coinAmount.toString()
            
            // 设置额外信息
            if (option.extraInfo != null) {
                tvExtraInfo.text = option.extraInfo
                tvExtraInfo.visibility = View.VISIBLE
            } else {
                tvExtraInfo.visibility = View.GONE
            }
            
            // 设置价格
            tvPrice.text = option.price
            
            // 设置折扣标签
            if (option.discountTag != null) {
                tvDiscountTag.text = option.discountTag
                tvDiscountTag.visibility = View.VISIBLE
            } else {
                tvDiscountTag.visibility = View.GONE
            }

            // 设置初始选中状态样式
            if (option.isRecommended) {
                layoutRoot.setBackgroundResource(com.xly.R.drawable.bg_wallet_card_border_selected)
                val selectedWidth = dp2px(115)
                cardRoot.layoutParams.width = selectedWidth
                cardRoot.requestLayout()
            } else {
                layoutRoot.setBackgroundResource(com.xly.R.drawable.bg_wallet_card_border)
                val normalWidth = dp2px(100)
                cardRoot.layoutParams.width = normalWidth
                cardRoot.requestLayout()
            }

            // 点击选择
            cardView.setOnClickListener {
                selectedOption = option
                updateRechargeSelection(cardView)
            }

            container.addView(cardView)
        }
    }

    /**
     * 更新充值选项选中状态（与VIP充值保持一致）
     */
    private fun updateRechargeSelection(selectedView: View) {
        val container = viewBind.llRechargeOptions
        val normalWidth = dp2px(100)
        val selectedWidth = dp2px(115)
        
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            val layoutRoot = child.findViewById<ViewGroup>(com.xly.R.id.layoutRoot)
            val cardRoot = child.findViewById<androidx.cardview.widget.CardView>(com.xly.R.id.cardRoot)
            
            if (layoutRoot != null && cardRoot != null) {
                if (child == selectedView) {
                    // 选中状态：淡金黄色背景、橙色边框，宽度变大
                    layoutRoot.setBackgroundResource(com.xly.R.drawable.bg_wallet_card_border_selected)
                    animateWidth(cardRoot, cardRoot.layoutParams.width, selectedWidth)
                } else {
                    // 未选中状态：灰色边框，宽度变小
                    layoutRoot.setBackgroundResource(com.xly.R.drawable.bg_wallet_card_border)
                    animateWidth(cardRoot, cardRoot.layoutParams.width, normalWidth)
                }
            }
        }
    }
    
    /**
     * 宽度动画（与VIP充值保持一致）
     */
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
    
    /**
     * dp转px工具函数
     */
    private fun dp2px(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    /**
     * 设置用途网格（2行4列）
     */
    private fun setupUsageGrid() {
        val gridContainer = viewBind.root.findViewById<LinearLayout>(com.xly.R.id.llUsageGrid)
        val row1 = gridContainer?.getChildAt(0) as? LinearLayout
        val row2 = gridContainer?.getChildAt(1) as? LinearLayout

        if (row1 != null && row2 != null) {
            // 第一行：前4个
            for (i in 0 until 4) {
                val usageItem = usageItems[i]
                val itemView = LayoutInflater.from(this)
                    .inflate(com.xly.R.layout.item_wallet_usage_item, row1, false)
                
                val ivIcon = itemView.findViewById<ImageView>(com.xly.R.id.ivUsageIcon)
                val tvText = itemView.findViewById<TextView>(com.xly.R.id.tvUsageText)
                
                ivIcon.setImageResource(usageItem.iconRes)
                tvText.text = usageItem.text
                
                row1.addView(itemView)
            }

            // 第二行：后4个
            for (i in 4 until 8) {
                val usageItem = usageItems[i]
                val itemView = LayoutInflater.from(this)
                    .inflate(com.xly.R.layout.item_wallet_usage_item, row2, false)
                
                val ivIcon = itemView.findViewById<ImageView>(com.xly.R.id.ivUsageIcon)
                val tvText = itemView.findViewById<TextView>(com.xly.R.id.tvUsageText)
                
                ivIcon.setImageResource(usageItem.iconRes)
                tvText.text = usageItem.text
                
                row2.addView(itemView)
            }
        }
    }
}
