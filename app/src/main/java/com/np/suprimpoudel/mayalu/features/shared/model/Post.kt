package com.np.suprimpoudel.mayalu.features.shared.model

import android.os.Parcel
import android.os.Parcelable

data class Post(
    var postId: String? = null,
    var postDate: String? = null,
    var postBy: String? = null,
    var postUserId: String? = null,
    var postUserPhotoUrl: String? = null,
    var postContent: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(postDate)
        parcel.writeString(postBy)
        parcel.writeString(postUserId)
        parcel.writeString(postUserPhotoUrl)
        parcel.writeString(postContent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
