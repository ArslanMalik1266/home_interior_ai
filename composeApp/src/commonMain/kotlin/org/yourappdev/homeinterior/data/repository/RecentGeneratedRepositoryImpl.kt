package org.yourappdev.homeinterior.data.repository

import kotlinx.coroutines.flow.Flow
import org.yourappdev.homeinterior.data.local.dao.RecentGeneratedDao
import org.yourappdev.homeinterior.data.local.entities.RecentGeneratedEntity
import org.yourappdev.homeinterior.domain.repo.RecentGeneratedRepository

class RecentGeneratedRepositoryImpl(
    private val recentGeneratedDao: RecentGeneratedDao
) : RecentGeneratedRepository {

    override fun getRecentGenerated(): Flow<List<RecentGeneratedEntity>> {
        return recentGeneratedDao.getRecentGenerated()
    }

    override suspend fun saveGenerated(generated: RecentGeneratedEntity): Long {  // ← Long return
        println("🟡 REPO_SAVE: Saving entity...")
        println("🟡 REPO_SAVE:   - ID = ${generated.id}")
        println("🟡 REPO_SAVE:   - URL = ${generated.imageUrl}")

        val newId = recentGeneratedDao.insertGenerated(generated)

        println("🟡 REPO_SAVE: ✅ Inserted with ID = $newId")
        return newId
    }

    override suspend fun deleteGeneratedById(id: Long) {
        recentGeneratedDao.deleteGeneratedById(id)
    }

}