package com.np.suprimpoudel.mayalu.features.shared.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.ItemMatchesBinding
import com.np.suprimpoudel.mayalu.features.listeners.UserCardClickListener
import com.np.suprimpoudel.mayalu.features.shared.model.User

class MatchesViewHolder(
    private val binding: ItemMatchesBinding,
    private val listener: UserCardClickListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User) {
        binding.user = user
        binding.executePendingBindings()

        Glide.with(binding.root)
            .load(user.profilePhotoUrl)
            .error(R.drawable.mayalu_app_icon)
            .placeholder(R.drawable.mayalu_app_icon)
            .into(binding.imvInputImage)

        itemView.setOnClickListener {
            listener.onUserCardClickListener(user)
        }
    }
}