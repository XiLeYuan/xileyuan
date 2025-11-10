package com.xly.business.message.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xly.R
import com.xly.business.message.model.Message
import com.xly.middlelibrary.widget.LYRoundImageView

class MessageAdapter(private val list: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val head: LYRoundImageView = itemView.findViewById(R.id.head)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_message_list_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = list[position]
        holder.name.text = msg.sender
        holder.message.text = msg.content
        holder.time.text = msg.time
        
        // 头像 - 加载本地 mipmap 资源
        if (msg.avatar != null) {
            val context = holder.itemView.context
            val resourceId = context.resources.getIdentifier(
                msg.avatar,
                "mipmap",
                context.packageName
            )
            if (resourceId != 0) {
                Glide.with(context)
                    .load(resourceId)
                    .circleCrop()
                    .into(holder.head)
            } else {
                // 如果资源不存在，使用默认头像
                holder.head.setImageResource(R.mipmap.head_img)
            }
        } else {
            // 如果没有头像，使用默认头像
            holder.head.setImageResource(R.mipmap.head_img)
        }
    }

    override fun getItemCount(): Int = list.size
}