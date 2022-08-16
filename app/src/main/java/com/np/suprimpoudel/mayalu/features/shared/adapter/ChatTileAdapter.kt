package com.np.suprimpoudel.mayalu.features.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.np.suprimpoudel.mayalu.databinding.ItemChatTileBinding
import com.np.suprimpoudel.mayalu.features.listeners.ChatTileClickListener
import com.np.suprimpoudel.mayalu.features.shared.model.Chat
import com.np.suprimpoudel.mayalu.features.shared.viewholder.ChatTileViewHolder

class ChatTileAdapter(
    private val chatList: MutableList<Chat>,
    private val listener: ChatTileClickListener,
) :
    RecyclerView.Adapter<ChatTileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatTileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChatTileViewHolder(ItemChatTileBinding.inflate(layoutInflater, parent, false),
            listener)
    }

    override fun onBindViewHolder(holder: ChatTileViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}