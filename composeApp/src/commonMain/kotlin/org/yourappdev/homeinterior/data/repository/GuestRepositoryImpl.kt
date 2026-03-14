package org.yourappdev.homeinterior.data.repository

import io.ktor.client.call.body
import org.yourappdev.homeinterior.data.mapper.toDomain
import org.yourappdev.homeinterior.data.remote.dto.GuestSessionDto
import org.yourappdev.homeinterior.data.remote.service.AuthService
import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GuestSession
import org.yourappdev.homeinterior.domain.repo.GuestRepository

class GuestRepositoryImpl(
    private val authService: AuthService
) : GuestRepository {

    override suspend fun registerGuest(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession> {
        return try {
            val response = authService.registerGuest(
                packageName = packageName,
                deviceId = deviceId
            ).body<GuestSessionDto>()
            ResultState.Success(response.toDomain())
        } catch (e: Exception) {
            println("DEBUG_GUEST_REPO: Exception = ${e.message}")
            ResultState.Error(e.message ?: "Unknown error")
        }
    }
}