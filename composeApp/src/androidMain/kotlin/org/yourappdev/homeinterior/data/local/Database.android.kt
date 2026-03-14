package org.yourappdev.homeinterior.data.local

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import okhttp3.internal.platform.PlatformRegistry.applicationContext
import org.yourappdev.homeinterior.utils.Constants
import org.yourappdev.homeinterior.utils.Constants.DB_NAME


fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(DB_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}