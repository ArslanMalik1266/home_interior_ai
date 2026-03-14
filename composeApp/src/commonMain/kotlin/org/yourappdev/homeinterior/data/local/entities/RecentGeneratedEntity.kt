package org.yourappdev.homeinterior.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "recent_generated")
data class RecentGeneratedEntity @OptIn(ExperimentalTime::class) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageBytes: ByteArray,
    val imageUrl: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)