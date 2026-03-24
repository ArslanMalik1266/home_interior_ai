package org.yourappdev.homeinterior.domain.repo

import kotlinx.coroutines.flow.Flow
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.entities.DraftEntity

interface DraftsRepository {
    fun getAllDrafts(): Flow<List<DraftEntity>>
    suspend fun saveDraft(draft: DraftEntity)
    suspend fun deleteDraft(draft: DraftEntity)
    suspend fun deleteDraftById(id: Long)
}