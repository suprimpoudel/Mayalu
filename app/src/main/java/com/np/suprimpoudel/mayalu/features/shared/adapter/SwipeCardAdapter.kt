package com.np.suprimpoudel.mayalu.features.shared.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.ItemSwipeBinding
import com.np.suprimpoudel.mayalu.features.listeners.SwipeCardOnClickListener
import com.np.suprimpoudel.mayalu.features.shared.model.User

class SwipeCardAdapter(
    context: Context,
    resourceId: Int,
    private val users: List<User>,
    private val listener: SwipeCardOnClickListener,
) : ArrayAdapter<User>(
    context, resourceId, users
) {

    val TAG = "DEBUG_DATA"

    private var binding: ItemSwipeBinding? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user = users[position]
        val v =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.item_swipe, parent, false)

        binding = ItemSwipeBinding.bind(v)

        Log.d(TAG, "getView: $user")
        binding?.txvDisplayName?.text = user.name
        binding?.imvProfilePicture?.let {
            Glide.with(context)
                .load(user.profilePhotoUrl)
                .into(it)
        }

        binding?.imgLikeBtn?.setOnClickListener {
            listener.onLikeButtonClicked(user)
        }

        binding?.imgDislikeBtn?.setOnClickListener {
            listener.onDislikeButtonClicked()
        }

        binding?.txvDisplayName?.setOnClickListener {
            listener.onSwipeCardClicked(user)
        }
        return v
    }
}