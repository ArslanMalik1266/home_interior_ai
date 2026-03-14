package org.yourappdev.homeinterior.data.repository

import io.ktor.client.call.body
import org.yourappdev.homeinterior.data.mapper.toDomain
import org.yourappdev.homeinterior.data.remote.service.RoomService
import org.yourappdev.homeinterior.domain.model.GenerateRoomResponse
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult
import org.yourappdev.homeinterior.domain.model.Rooms
import org.yourappdev.homeinterior.domain.repo.RoomsRepository

class RoomRepositoryImpl(val roomService: RoomService) : RoomsRepository {
    override suspend fun getRoomsList(): Rooms {
        val response = roomService.getRooms().body<Rooms>()
        return response
    }


}