package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.GuestSession
import org.yourappdev.homeinterior.domain.repo.GuestRepository

class RegisterGuestUseCase(
    private val repository: GuestRepository
) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String
    ): ResultState<GuestSession> {
        return repository.registerGuest(packageName, deviceId)
    }
}