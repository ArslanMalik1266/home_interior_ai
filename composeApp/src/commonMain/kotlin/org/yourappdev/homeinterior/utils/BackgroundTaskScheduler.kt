package org.yourappdev.homeinterior.utils

interface BackgroundTaskScheduler {
    /**
     * @param taskId: Image generation ki unique ID
     * @param delaySeconds: Jitni der baad check karna hai (ETA)
     */
    fun scheduleImageStatusCheck(taskId: String, delaySeconds: Long)
}