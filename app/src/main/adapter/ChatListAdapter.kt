package com.jspp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jspp.databinding.ItemChatListBinding
import com.jspp.dbbean.chat.ChatBean

class ChatListAdapter(
    private val onItemClick: (ChatBean) -> Unit,
    private val onItemLongClick: (ChatBean) -> Unit,
    private val onMarkAsRead: (ChatBean) -> Unit,
    private val onTopClick: (ChatBean) -> Unit,
    private val onDeleteClick: (ChatBean) -> Unit
) : ListAdapter<ChatBean, ChatListAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // 部分更新
            val chatBean = getItem(position)
            val payload = payloads.first()
            
            when (payload) {
                "unread_count" -> holder.updateUnreadCount(chatBean)
                "message_text" -> holder.updateMessageText(chatBean)
                "time" -> holder.updateTime(chatBean)
                "top_status" -> holder.updateTopStatus(chatBean)
                else -> super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    /**
     * 方法1：更新指定位置的Item
     */
    fun updateItem(position: Int, newChatBean: ChatBean) {
        if (position in 0 until currentList.size) {
            val newList = currentList.toMutableList()
            newList[position] = newChatBean
            submitList(newList)
        }
    }

    /**
     * 方法2：根据ID更新Item
     */
    fun updateItemById(chatId: String, newChatBean: ChatBean) {
        val position = currentList.indexOfFirst { it.id == chatId }
        if (position != -1) {
            updateItem(position, newChatBean)
        }
    }

    /**
     * 方法3：只更新未读消息数
     */
    fun updateUnreadCount(position: Int, unreadCount: Int) {
        if (position in 0 until currentList.size) {
            val currentItem = currentList[position]
            val updatedItem = currentItem.copy(unreadCount = unreadCount)
            updateItem(position, updatedItem)
        }
    }

    /**
     * 方法4：只更新最后消息
     */
    fun updateLastMessage(position: Int, newText: String, newTime: Long) {
        if (position in 0 until currentList.size) {
            val currentItem = currentList[position]
            val updatedItem = currentItem.copy(
                text = newText,
                serverTime = newTime
            )
            updateItem(position, updatedItem)
        }
    }

    /**
     * 方法5：更新置顶状态
     */
    fun updateTopStatus(position: Int, isTop: Boolean) {
        if (position in 0 until currentList.size) {
            val currentItem = currentList[position]
            val updatedItem = currentItem.copy(isTop = isTop)
            updateItem(position, updatedItem)
        }
    }

    /**
     * 方法6：使用notifyItemChanged直接通知（适用于简单更新）
     */
    fun notifyItemChanged(position: Int, payload: Any? = null) {
        if (position in 0 until currentList.size) {
            super.notifyItemChanged(position, payload)
        }
    }

    /**
     * 方法7：批量更新多个Item
     */
    fun updateItems(updates: List<Pair<Int, ChatBean>>) {
        val newList = currentList.toMutableList()
        updates.forEach { (position, newItem) ->
            if (position in 0 until newList.size) {
                newList[position] = newItem
            }
        }
        submitList(newList)
    }

    /**
     * 方法8：更新指定范围的Item
     */
    fun updateItemRange(startPosition: Int, itemCount: Int, newItems: List<ChatBean>) {
        if (startPosition >= 0 && startPosition + itemCount <= currentList.size) {
            val newList = currentList.toMutableList()
            for (i in 0 until itemCount) {
                if (startPosition + i < newList.size && i < newItems.size) {
                    newList[startPosition + i] = newItems[i]
                }
            }
            submitList(newList)
        }
    }

    inner class ChatViewHolder(
        private val binding: ItemChatListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatBean: ChatBean) {
            // 设置基本信息
            binding.tvChatName.text = chatBean.name
            binding.tvLastMessage.text = chatBean.text
            binding.tvTime.text = formatTime(chatBean.serverTime)
            
            // 设置未读消息数
            updateUnreadCount(chatBean)
            
            // 设置置顶状态
            updateTopStatus(chatBean)
            
            // 设置点击事件
            binding.root.setOnClickListener {
                onItemClick(chatBean)
            }
            
            binding.root.setOnLongClickListener {
                onItemLongClick(chatBean)
                true
            }
        }
        
        fun updateUnreadCount(chatBean: ChatBean) {
            if (chatBean.unreadCount > 0) {
                binding.tvUnreadCount.visibility = View.VISIBLE
                binding.tvUnreadCount.text = if (chatBean.unreadCount > 99) "99+" else chatBean.unreadCount.toString()
            } else {
                binding.tvUnreadCount.visibility = View.GONE
            }
        }
        
        fun updateMessageText(chatBean: ChatBean) {
            binding.tvLastMessage.text = chatBean.text
        }
        
        fun updateTime(chatBean: ChatBean) {
            binding.tvTime.text = formatTime(chatBean.serverTime)
        }
        
        fun updateTopStatus(chatBean: ChatBean) {
            binding.ivTop.visibility = if (chatBean.isTop) View.VISIBLE else View.GONE
            binding.root.setBackgroundResource(
                if (chatBean.isTop) R.drawable.chat_item_bg_top_selector 
                else R.drawable.chat_item_bg_selector
            )
        }
        
        private fun formatTime(timestamp: Long): String {
            // 实现时间格式化逻辑
            return DateUtils.formatChatDate(timestamp)
        }
    }
}

/**
 * DiffUtil回调
 */
class ChatDiffCallback : DiffUtil.ItemCallback<ChatBean>() {
    
    override fun areItemsTheSame(oldItem: ChatBean, newItem: ChatBean): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: ChatBean, newItem: ChatBean): Boolean {
        return oldItem.text == newItem.text &&
                oldItem.unreadCount == newItem.unreadCount &&
                oldItem.serverTime == newItem.serverTime &&
                oldItem.isTop == newItem.isTop &&
                oldItem.isOperateUnRead == newItem.isOperateUnRead
    }
    
    override fun getChangePayload(oldItem: ChatBean, newItem: ChatBean): Any? {
        return when {
            oldItem.unreadCount != newItem.unreadCount -> "unread_count"
            oldItem.text != newItem.text -> "message_text"
            oldItem.serverTime != newItem.serverTime -> "time"
            oldItem.isTop != newItem.isTop -> "top_status"
            else -> null
        }
    }
} 