package com.np.suprimpoudel.mayalu.features.listeners

import com.np.suprimpoudel.mayalu.features.shared.model.Chat

interface ChatTileClickListener {
    fun onChatTileClicked(chat: Chat)
}