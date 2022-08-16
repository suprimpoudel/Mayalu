package com.np.suprimpoudel.mayalu.features.shared.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.np.suprimpoudel.mayalu.databinding.ItemPostBinding
import com.np.suprimpoudel.mayalu.features.shared.model.Post
import com.np.suprimpoudel.mayalu.features.shared.viewholder.PostViewHolder

class PostAdapter(
    private val postsList: MutableList<Post>,
    private val currentUserId: String,
    val onDeleteIconClicked: (Post, position: Int) -> Unit,
    val onEditIconClicked: (Post) -> Unit,
) :
    RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(ItemPostBinding.inflate(layoutInflater, parent, false), currentUserId)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList[position]
        holder.bind(post)

        holder.binding.imvPostDelete.setOnClickListener {
            Log.d("DEBUG_DATA", "onBindViewHolder: Delete")
            onDeleteIconClicked(post, position)
        }
        holder.binding.imvPostEdit.setOnClickListener {
            onEditIconClicked(post)
        }
    }

    override fun getItemCount(): Int = postsList.size
}