package com.np.suprimpoudel.mayalu.features.shared.listeners

import com.np.suprimpoudel.mayalu.features.shared.model.Photo

interface PhotoSelectListener {
    fun getSelectedPhoto(photo: Photo)
}