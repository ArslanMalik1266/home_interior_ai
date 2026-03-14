package org.yourappdev.homeinterior

import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.getDatabaseBuilder
import org.yourappdev.homeinterior.data.local.getRoomDatabase

actual fun platformModule(): Module = module{
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(androidContext())
    }
}