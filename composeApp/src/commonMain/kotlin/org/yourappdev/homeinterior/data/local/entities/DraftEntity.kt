package org.yourappdev.homeinterior.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userImageBytes: ByteArray?,
    val roomType: String,
    val style: String,
    val paletteId: Int,
    val currentPage: Int,
    val createdAt: Long
)