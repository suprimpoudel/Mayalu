package com.np.suprimpoudel.mayalu.features.listeners

import com.np.suprimpoudel.mayalu.features.shared.model.User

interface SwipeCardOnClickListener {
    fun onLikeButtonClicked(user: User)
    fun onDislikeButtonClicked()
    fun onSwipeCardClicked(user: User)
}