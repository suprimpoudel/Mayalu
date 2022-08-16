package com.np.suprimpoudel.mayalu.features.shared.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.ItemGalleryPhotoBinding
import com.np.suprimpoudel.mayalu.features.shared.model.Photo

class GalleryViewHolder(
    val binding: ItemGalleryPhotoBinding,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(photo: Photo) {
        Glide.with(binding.imvPhoto.context)
            .load(photo.contentUri)
            .placeholder(R.drawable.mayalu_app_icon)
            .error(R.drawable.mayalu_app_icon)
            .into(binding.imvPhoto)
    }
}