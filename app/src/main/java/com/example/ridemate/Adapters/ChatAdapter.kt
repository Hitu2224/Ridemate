package com.example.ridemate.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ridemate.DataClasses.MessageModel
import com.example.ridemate.R

class ChatAdapter(private val currentUserId: String) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var messages = listOf<MessageModel>()

    fun setMessages(newMessages: List<MessageModel>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Yahan 'android.R.layout' ki jagah sirf 'R.layout.item_chat' use karein
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]

        // Aapki XML wali IDs
        val tvMessage = holder.itemView.findViewById<TextView>(R.id.tvMessage)
        val container = holder.itemView.findViewById<LinearLayout>(R.id.messageContainer)

        tvMessage.text = msg.message

        if (msg.senderId == currentUserId) {
            // Mera Message: Right side alignment + Blue Bubble
            container.gravity = Gravity.END
            tvMessage.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3B82F6"))
            tvMessage.setTextColor(Color.WHITE)
        } else {
            // Samne wale ka Message: Left side alignment + Gray Bubble
            container.gravity = Gravity.START
            tvMessage.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E2E8F0"))
            tvMessage.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = messages.size

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view)
}