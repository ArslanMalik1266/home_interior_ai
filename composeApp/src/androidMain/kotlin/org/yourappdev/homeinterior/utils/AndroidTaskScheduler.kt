package org.yourappdev.homeinterior.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AndroidTaskScheduler(private val context: Context) : BackgroundTaskScheduler {


    override fun scheduleImageStatusCheck(taskId: String, delaySeconds: Long) {
        println("WORKER_DEBUG: Scheduling task for ID: $taskId with delay: $delaySeconds seconds")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Internet hona zaroori hai
            .build()

        val data = Data.Builder()
            .putString("TASK_ID", taskId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ImageStatusWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS) // ETA ka wait karega
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "image_check_$taskId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        println("WORKER_DEBUG: WorkRequest enqueued successfully for $taskId")
    }
}