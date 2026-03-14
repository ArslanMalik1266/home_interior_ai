package org.yourappdev.homeinterior.data.repository

import io.ktor.utils.io.core.toByteArray
import org.yourappdev.homeinterior.data.remote.service.RoomService
import org.yourappdev.homeinterior.data.remote.dto.GenerateRoomRequestDto
import org.yourappdev.homeinterior.data.mapper.toDomain
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GenerateRoomRequest
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult
import org.yourappdev.homeinterior.domain.repo.GenerateRoomRepository
import org.yourappdev.homeinterior.utils.toBase64

class GenerateRoomRepositoryImpl(
    private val roomService: RoomService
) : GenerateRoomRepository {

    override suspend fun generateRoom(
        request: GenerateRoomRequest
    ): ResultState<GenerateRoomResult> {
        return try {
            val dto = roomService.generateRoom(
                initImage  = request.initImage,
                prompt = request.prompt,
                strength = request.strength
            )
            ResultState.Success(dto.toDomain())
        } catch (e: Exception) {
            println("DEBUG_REPO: ERROR -> ${e.message}")
            ResultState.Failure(e.message ?: "Unknown error")
        }
    }

    override suspend fun fetchResult(
        fetchUrl: String
    ): ResultState<GenerateRoomResult> {
        return try {
            val dto = roomService.fetchGeneratedRoom(fetchUrl)
            ResultState.Success(dto.toDomain())
        } catch (e: Exception) {
            println("DEBUG_REPO: FETCH ERROR -> ${e.message}")
            ResultState.Failure(e.message ?: "Unknown error")
        }
    }
}