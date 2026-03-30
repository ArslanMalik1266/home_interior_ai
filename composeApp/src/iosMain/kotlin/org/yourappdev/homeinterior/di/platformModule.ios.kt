package org.yourappdev.homeinterior.di

import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.getDatabaseBuilder
import org.yourappdev.homeinterior.utils.BackgroundTaskScheduler
import platform.BackgroundTasks.BGTaskScheduler
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.Foundation.NSDate
import platform.Foundation.dateByAddingTimeInterval

@OptIn(ExperimentalForeignApi::class)
actual fun platformModule(): Module = module {

    // ✅ iOS Database Builder
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }

    single<BackgroundTaskScheduler> {
        object : BackgroundTaskScheduler {
            override fun scheduleImageStatusCheck(taskId: String, delaySeconds: Long) {
                val identifier = "org.yourappdev.homeinterior.checkStatus"
                val request = BGAppRefreshTaskRequest(identifier).apply {
                    earliestBeginDate = NSDate().dateByAddingTimeInterval(delaySeconds.toDouble())
                }
                try {
                    BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
                    println("✅ iOS Background Task Scheduled: $taskId")
                } catch (e: Exception) {
                    println("❌ iOS Schedule Error: ${e.message}")
                }
            }
        }
    }
}
