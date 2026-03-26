package org.yourappdev.homeinterior.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.yourappdev.homeinterior.data.local.StringListConverter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "recent_generated")
@TypeConverters(StringListConverter::class)
data class RecentGeneratedEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localPaths: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val bundleId: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)