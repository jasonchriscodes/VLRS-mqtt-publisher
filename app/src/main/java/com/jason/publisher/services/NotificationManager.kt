package com.jason.publisher.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jason.publisher.R

class NotificationManager(private val context: Context) {

    private val channelName = "My Channel"

    private fun createNotificationChannel(channelId: String, isMessage: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance =
                if (isMessage) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply {
                description = "My notification channel"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        channelId: String,
        notificationId: Int,
        title: String,
        message: String,
        isMessage: Boolean
    ) {
        val icon = if (isMessage) android.R.drawable.ic_dialog_info else R.drawable.ic_signal
        val priority = if (isMessage) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
        createNotificationChannel(channelId, isMessage)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, builder.build())
    }
}
