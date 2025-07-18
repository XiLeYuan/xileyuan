package com.xly.business.vip.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.business.vip.model.VipRechargeOption
import com.xly.R

class VipRechargeAdapter(
    private val list: List<VipRechargeOption>,
    private val onItemClick: (VipRechargeOption) -> Unit
) : RecyclerView.Adapter<VipRechargeAdapter.VipViewHolder>() {

    inner class VipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vip_recharge_container_item, parent, false)
        return VipViewHolder(view)
    }

    override fun onBindViewHolder(holder: VipViewHolder, position: Int) {
        val option = list[position]
        holder.tvAmount.text = option.amount
        holder.tvDesc.text = option.desc
        holder.itemView.setOnClickListener { onItemClick(option) }
    }

    override fun getItemCount(): Int = list.size
}