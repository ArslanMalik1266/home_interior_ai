package org.yourappdev.homeinterior.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.yourappdev.homeinterior.data.local.dao.ProfileDao
import org.yourappdev.homeinterior.data.local.dao.DraftDao // Import add karein
import org.yourappdev.homeinterior.data.local.dao.RecentGeneratedDao
import org.yourappdev.homeinterior.data.local.entities.UserInfoEntity
import org.yourappdev.homeinterior.data.local.entities.DraftEntity // Import add karein
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity

@Database(
    entities = [DraftEntity::class,
               RecentGeneratedEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun draftDao(): DraftDao
    abstract fun recentGeneratedDao(): RecentGeneratedDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.Default)
    .build()