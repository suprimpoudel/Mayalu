package com.np.suprimpoudel.mayalu.features.shared.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.np.suprimpoudel.mayalu.R

class FirebaseService : FirebaseMessagingService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body

        val CHANNEL_ID = "MATCHED"
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Message Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notificationsBuilder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.mayalu_app_icon)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, notificationsBuilder.build())

        super.onMessageReceived(remoteMessage)
    }
}
