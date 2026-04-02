package org.yourappdev.homeinterior.utils

//expect fun requestNotificationPermission()

expect object NotificationManager {
    fun initialize()
    fun notifyIfBackground()
    fun isAppInBackground(): Boolean
    fun isNotificationsEnabled(): Boolean
    fun setNotificationsEnabled(enabled: Boolean)
}