package com.alitazik.unity_notification_manager_plugin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.unity3d.player.UnityPlayer

class NotificationPlugin {
    companion object {
        private const val TAG = "NotificationPlugin"

        // Unique channel ID for notifications
        private const val CHANNEL_ID = "UnityNotificationChannel"

        // Method to create notification channel (required for Android Oreo and above)
        @JvmStatic
        fun createNotificationChannel() {
            val context = UnityPlayer.currentActivity
            Log.d(TAG, "Activity set successfully: $context")

            // Only create channel for Android Oreo and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.O")
                val name = "Unity Notifications"
                val descriptionText = "Notifications from Unity Application"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                Log.d(TAG, "createNotificationChannel: $CHANNEL_ID")

                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Method to show a notification
        @JvmStatic
        fun showNotification(title: String, message: String): Boolean {
            Log.d(TAG, "showNotification start")
            val context = UnityPlayer.currentActivity

            // Check for permission first (for Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "showNotification permission garantied")
                    // Request permission
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1 // Request code
                    )
                    return false
                }
            }
            Log.d(TAG, " NotificationCompat.Builder start")
            // Build the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


            // Show the notification
            try {
                with(NotificationManagerCompat.from(context)) {
                    notify(System.currentTimeMillis().toInt(), builder.build())
                }

                Log.d(TAG, " NotificationCompat.Builder done return true")
                return true
            } catch (e: SecurityException) {
                Log.e(TAG, "showNotification failed", e)
                // Handle permission denial
                return false
            }
        }
    }
}