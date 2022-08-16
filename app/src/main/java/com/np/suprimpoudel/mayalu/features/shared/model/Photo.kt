package com.np.suprimpoudel.mayalu.features.shared.model

import android.net.Uri

data class Photo(
    val id: Long? = null,
    val name: String? = null,
    val size: String? = null,
    val dateTaken: String? = null,
    val contentUri: Uri? = null,
)