package com.np.suprimpoudel.mayalu.network

import com.np.suprimpoudel.mayalu.features.shared.model.response.NotificationResponse
import com.np.suprimpoudel.mayalu.utils.constants.APIConstants
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST(APIConstants.SEND_NOTIFICATION)
    suspend fun sendNotification(
        @Body requestBody: RequestBody,
    ): Response<NotificationResponse>
}