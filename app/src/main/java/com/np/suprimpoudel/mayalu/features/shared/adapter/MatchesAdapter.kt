package com.np.suprimpoudel.mayalu.features.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.np.suprimpoudel.mayalu.databinding.ItemMatchesBinding
import com.np.suprimpoudel.mayalu.features.listeners.UserCardClickListener
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.shared.viewholder.MatchesViewHolder

class MatchesAdapter(
    private val matchedUserList: MutableList<User>,
    private val listener: UserCardClickListener
) :
    RecyclerView.Adapter<MatchesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MatchesViewHolder(ItemMatchesBinding.inflate(layoutInflater, parent, false), listener)
    }

    override fun onBindViewHolder(holder: MatchesViewHolder, position: Int) {
        val user = matchedUserList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = matchedUserList.size
}