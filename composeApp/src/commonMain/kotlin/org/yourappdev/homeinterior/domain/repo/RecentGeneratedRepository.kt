package org.yourappdev.homeinterior.domain.repo

import kotlinx.coroutines.flow.Flow
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity

interface RecentGeneratedRepository {
    fun getRecentGenerated(): Flow<List<RecentGeneratedEntity>>
    suspend fun saveGenerated(generated: RecentGeneratedEntity): Long
    suspend fun deleteGeneratedById(id: Long)

}