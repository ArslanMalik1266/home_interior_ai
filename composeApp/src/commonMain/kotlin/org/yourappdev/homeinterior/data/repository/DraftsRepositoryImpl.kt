package org.yourappdev.homeinterior.data.repository

import kotlinx.coroutines.flow.Flow
import org.yourappdev.homeinterior.data.local.AppDatabase
import org.yourappdev.homeinterior.data.local.entities.DraftEntity
import org.yourappdev.homeinterior.domain.repo.DraftsRepository

class DraftsRepositoryImpl(private val db: AppDatabase) : DraftsRepository {
    override fun getAllDrafts(): Flow<List<DraftEntity>> = db.draftDao().getAllDrafts()

    override suspend fun saveDraft(draft: DraftEntity) = db.draftDao().insertDraft(draft)

    override suspend fun deleteDraft(draft: DraftEntity) = db.draftDao().deleteDraft(draft)
    override suspend fun deleteDraftById(id: Long) = db.draftDao().deleteDraftById(id)


}