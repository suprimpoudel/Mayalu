package com.np.suprimpoudel.mayalu.features.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.np.suprimpoudel.mayalu.databinding.ItemGalleryPhotoBinding
import com.np.suprimpoudel.mayalu.features.shared.model.Photo
import com.np.suprimpoudel.mayalu.features.shared.viewholder.GalleryViewHolder

class GalleryAdapter(
    private val photoList: MutableList<Photo>,
    private val getPhoto: (Photo) -> Unit,
) :
    RecyclerView.Adapter<GalleryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GalleryViewHolder(ItemGalleryPhotoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(photoList[position])

        holder.binding.root.setOnClickListener {
            getPhoto(photoList[position])
        }
    }

    override fun getItemCount(): Int = photoList.size
}