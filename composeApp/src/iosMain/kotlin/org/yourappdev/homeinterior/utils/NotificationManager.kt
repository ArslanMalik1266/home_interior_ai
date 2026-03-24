package org.yourappdev.homeinterior.utils

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual fun requestNotificationPermission() {
    val center = UNUserNotificationCenter.currentNotificationCenter()
    center.requestAuthorizationWithOptions(
        UNAuthorizationOptionAlert or
                UNAuthorizationOptionSound or
                UNAuthorizationOptionBadge
    ) { granted, error ->
        println("🔔 iOS Notification Permission: $granted")
    }
}

actual fun sendLocalNotification(
    title: String,
    body: String,
    notificationId: Int
) {
    val content = UNMutableNotificationContent().apply {
        setTitle(title)
        setBody(body)
        setSound(UNNotificationSound.defaultSound())
    }

    // 1 second baad notification aaye
    val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
        timeInterval = 1.0,
        repeats = false
    )

    val request = UNNotificationRequest.requestWithIdentifier(
        identifier = "image_ready_$notificationId",
        content = content,
        trigger = trigger
    )

    UNUserNotificationCenter.currentNotificationCenter()
        .addNotificationRequest(request) { error ->
            error?.let { println("❌ iOS Notification Error: $it") }
        }
}