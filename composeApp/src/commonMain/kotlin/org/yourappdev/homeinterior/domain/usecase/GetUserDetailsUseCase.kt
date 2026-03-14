package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.data.remote.util.ResultState
import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.repo.AuthRepository

class GetUserProfileUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<DeviceLinkResult> {
        return repository.getProfileAndCredits(
            packageName = packageName,
            deviceId = deviceId,
            userEmail = userEmail,
            authProvider = authProvider
        )
    }
}