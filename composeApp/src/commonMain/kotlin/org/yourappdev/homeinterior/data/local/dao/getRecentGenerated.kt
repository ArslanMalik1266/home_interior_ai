package org.yourappdev.homeinterior.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity

@Dao
interface RecentGeneratedDao {

    @Query("SELECT * FROM recent_generated ORDER BY createdAt DESC")
    fun getRecentGenerated(): Flow<List<RecentGeneratedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenerated(generated: RecentGeneratedEntity) : Long

    @Query("DELETE FROM recent_generated WHERE id = :id")
    suspend fun deleteGeneratedById(id: Long)
}