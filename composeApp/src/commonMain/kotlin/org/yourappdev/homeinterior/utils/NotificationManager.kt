package org.yourappdev.homeinterior.utils

expect fun requestNotificationPermission()

expect fun sendLocalNotification(
    title: String,
    body: String,
    notificationId: Int = 1
)