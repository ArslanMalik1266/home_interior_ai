package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.utils.BackgroundTaskScheduler

class StartImageTrackingUseCase(private val scheduler: BackgroundTaskScheduler) {
    operator fun invoke(taskId: String, etaSeconds: Long) {
        scheduler.scheduleImageStatusCheck(taskId, etaSeconds)
    }
}