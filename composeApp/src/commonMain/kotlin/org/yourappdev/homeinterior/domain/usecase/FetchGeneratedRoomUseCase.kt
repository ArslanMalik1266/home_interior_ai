package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult
import org.yourappdev.homeinterior.domain.repo.GenerateRoomRepository

class FetchGeneratedRoomUseCase(
    private val repository: GenerateRoomRepository
) {
    suspend operator fun invoke(
        fetchUrl: String
    ): ResultState<GenerateRoomResult> {
        return repository.fetchResult(fetchUrl)
    }
}