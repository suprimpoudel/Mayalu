package com.np.suprimpoudel.mayalu.features.shared.model.request

import com.google.gson.annotations.SerializedName

data
class NotificationRequest(
    @SerializedName("registration_ids")
    var registrationIds: ArrayList<String> = arrayListOf(),

    @SerializedName("notification")
    var notification: Notification? = Notification(),
)

data class Notification(
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
)