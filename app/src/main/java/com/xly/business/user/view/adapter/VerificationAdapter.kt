package com.xly.business.user.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xly.business.user.view.VerificationActivity
import com.xly.R

class VerificationAdapter(
    private val verificationTypes: List<VerificationActivity.VerificationType>,
    private val onItemClick: (VerificationActivity.VerificationType) -> Unit
) : RecyclerView.Adapter<VerificationAdapter.VerificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_verification_card, parent, false)
        return VerificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: VerificationViewHolder, position: Int) {
        holder.bind(verificationTypes[position])
    }

    override fun getItemCount(): Int = verificationTypes.size

    inner class VerificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(verificationType: VerificationActivity.VerificationType) {
            ivIcon.setImageResource(verificationType.iconRes)
            tvTitle.text = verificationType.title
            tvDescription.text = verificationType.description

            if (verificationType.isVerified) {
                tvStatus.text = "已认证"
                tvStatus.setTextColor(itemView.context.getColor(R.color.text_success))
            } else {
                tvStatus.text = "未认证"
                tvStatus.setTextColor(itemView.context.getColor(R.color.text_hint))
            }

            itemView.setOnClickListener {
                onItemClick(verificationType)
            }
        }
    }
}

