package com.np.suprimpoudel.mayalu.features.shared.repository.user

import com.np.suprimpoudel.mayalu.features.shared.model.request.NotificationRequest
import com.np.suprimpoudel.mayalu.features.shared.repository.base.BaseRepository
import com.np.suprimpoudel.mayalu.network.ApiService
import javax.inject.Inject

class SwipeRepository @Inject constructor(
    private val apiService: ApiService,
) : BaseRepository() {
    suspend fun sendMatchedNotification(notificationRequest: NotificationRequest) =
        apiService.sendNotification(getGlobalUtils(notificationRequest))
}