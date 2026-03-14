package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomRequest
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult
import org.yourappdev.homeinterior.domain.repo.GenerateRoomRepository

class GenerateRoomUseCase(
    private val repository: GenerateRoomRepository
) {
    suspend operator fun invoke(
        request: GenerateRoomRequest
    ): ResultState<GenerateRoomResult> {
        return repository.generateRoom(request)
    }
}