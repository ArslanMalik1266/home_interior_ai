package org.yourappdev.homeinterior.utils

import android.Manifest
import android.R
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

private const val CHANNEL_ID   = "image_gen_channel"
private const val CHANNEL_NAME = "Image Generation"
private const val NOTIF_ID     = 1001

object AppContext {
    private lateinit var _context: Context
    fun set(context: Context) { _context = context.applicationContext }
    fun get(): Context = _context
}

actual object NotificationManager {

    actual fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Image generation complete notification"
                enableVibration(true)
                enableLights(true)
            }
            val manager = AppContext.get()
                .getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    actual fun notifyIfBackground() {
        showNotification()
    }

    actual fun isAppInBackground(): Boolean {
        return try {
            val appProcesses = (AppContext.get()
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .runningAppProcesses ?: return true

            val packageName = AppContext.get().packageName
            appProcesses.none {
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && it.processName == packageName
            }
        } catch (e: Exception) {
            true // Error aaye to background samjho aur notification do
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification() {
        println("🚀 Step 1: showNotification() called")
        val context = AppContext.get()

        // 1. Check if notifications are enabled for the whole app
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            println("❌ Error: App notifications are DISABLED in System Settings!")
            return
        }

        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        println("📝 Step 2: Building Notification (Channel ID: $CHANNEL_ID)")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using standard system icon for testing
            .setContentTitle("✨ Image Ready!")
            .setContentText("Your interior image has been successfully generated!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your interior design is ready! Tap to view your generated space. 🏠")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Ensures sound and vibration
            .build()

        try {
            println("Sending notification to system... ID: $NOTIF_ID")

            // Using direct System Service for better reliability
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.notify(NOTIF_ID, notification)

            println("✅ Step 3: Notification successfully sent to the system!")
        } catch (e: Exception) {
            println("⚠️ Error during notify: ${e.message}")
            e.printStackTrace()
        }
    }
}