package com.xly.business.vip.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.R
import com.xly.business.vip.model.VipRechargeOption

class VipRechargeAdapter(
    private val list: List<VipRechargeOption>,
    private val onItemClick: (VipRechargeOption) -> Unit
) : RecyclerView.Adapter<VipRechargeAdapter.VipViewHolder>() {

    inner class VipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTag: TextView = itemView.findViewById(R.id.tvTag)
        val tvCurrentPrice: TextView = itemView.findViewById(R.id.tvCurrentPrice)
        val tvOriginalPrice: TextView = itemView.findViewById(R.id.tvOriginalPrice)
        val tvPerMonth: TextView = itemView.findViewById(R.id.tvPerMonth)
        val root: View = itemView.findViewById(R.id.layoutRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vip_recharge_container_item, parent, false)
        return VipViewHolder(view)
    }

    override fun onBindViewHolder(holder: VipViewHolder, position: Int) {
        val option = list[position]
        holder.tvTag.text = option.tag
        holder.tvCurrentPrice.text = option.currentPrice
        holder.tvPerMonth.text = option.perMonthText

        if (option.originalPrice.isNullOrEmpty()) {
            holder.tvOriginalPrice.visibility = View.GONE
        } else {
            holder.tvOriginalPrice.visibility = View.VISIBLE
            holder.tvOriginalPrice.text = option.originalPrice
        }

        // 主推高亮边框
        if (option.isRecommended) {
            holder.root.setBackgroundResource(R.drawable.vip_card_item_border_primary)
        } else {
            holder.root.setBackgroundResource(R.drawable.vip_card_item_border)
        }

        holder.itemView.setOnClickListener { onItemClick(option) }
    }

    override fun getItemCount(): Int = list.size
}