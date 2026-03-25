package org.yourappdev.homeinterior.utils

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class ImageStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        // ✅ 1. Sab se pehle log check karein
        println("🚀 WORKER_DEBUG: Background Worker Trigger ho gaya!")

        // ✅ 2. Task ID nikalain (Keys Case-Sensitive hain: "TASK_ID")
        val taskId = inputData.getString("TASK_ID")
        println("WORKER_DEBUG: Worker started running for Task ID: $taskId")

        if (taskId == null) {
            println("❌ WORKER_DEBUG: Task ID missing hai!")
            return Result.failure()
        }

        println("📌 WORKER_DEBUG: Task [ $taskId ] process ho raha hai...")

        return try {
            // ✅ 3. Background Notification Logic
            if (NotificationManager.isAppInBackground()) {
                println("✨ WORKER_DEBUG: Notification bhej raha hoon...")
                NotificationManager.notifyIfBackground()
            } else {
                println("📱 WORKER_DEBUG: App foreground mein hai, notification skip.")
            }

            Result.success()
        } catch (e: Exception) {
            println("⚠️ WORKER_DEBUG: Error: ${e.message}")
            Result.retry()
        }
    }
}