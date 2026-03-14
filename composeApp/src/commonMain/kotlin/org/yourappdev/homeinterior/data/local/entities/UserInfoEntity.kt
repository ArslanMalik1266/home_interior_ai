package org.yourappdev.homeinterior.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import org.yourappdev.homeinterior.utils.Constants
import org.yourappdev.homeinterior.utils.Constants.TABLE_USERS

@Entity(tableName = TABLE_USERS)
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val fullname: String,
    val email: String,
    val token: String? = null,
)