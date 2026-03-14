package org.yourappdev.homeinterior.domain.repo

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomRequest
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult

interface GenerateRoomRepository {
    suspend fun generateRoom(request: GenerateRoomRequest): ResultState<GenerateRoomResult>
    suspend fun fetchResult(fetchUrl: String): ResultState<GenerateRoomResult>
}