package org.yourappdev.homeinterior.di

import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.getDatabaseBuilder
import org.yourappdev.homeinterior.utils.AndroidTaskScheduler
import org.yourappdev.homeinterior.utils.BackgroundTaskScheduler
import org.yourappdev.homeinterior.utils.ImageStatusWorker

actual fun platformModule(): Module = module{
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(androidContext())
    }
    single<BackgroundTaskScheduler> { AndroidTaskScheduler(get()) }

    // Worker register karein
    worker { ImageStatusWorker(get(), get()) }}