package org.yourappdev.homeinterior.di

import androidx.room.RoomDatabase
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.getRoomDatabase


val databaseModule = module {
    single { getRoomDatabase(get<RoomDatabase.Builder<AppDatabase>>()) }
    single { get<AppDatabase>().draftDao() }
    single { get<AppDatabase>().recentGeneratedDao() }
    single<Settings> { Settings() }

}