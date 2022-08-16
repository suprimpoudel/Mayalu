package com.np.suprimpoudel.mayalu.features.shared.viewholder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.ItemMessageOthersBinding
import com.np.suprimpoudel.mayalu.databinding.ItemMessageSelfBinding
import com.np.suprimpoudel.mayalu.features.shared.model.ChatData

class MessageOthersViewHolder(private val binding: ItemMessageOthersBinding) :
    BaseViewHolder(binding.root) {
        fun bind(chatData: ChatData) {
            if(chatData.pictureUrl?.isNotEmpty() == true) {
                binding.imvImage.isVisible = true
                Glide.with(binding.imvImage)
                    .load(chatData.pictureUrl)
                    .error(R.drawable.mayalu_app_icon)
                    .placeholder(R.drawable.mayalu_app_icon)
                    .into(binding.imvImage)
            }
            if(chatData.message?.isEmpty() == true) {
                binding.txvMessage.isVisible = false
            }
            binding.txvMessage.text = chatData.message
            binding.txvTimeStamp.text = chatData.timeStamp
        }
}