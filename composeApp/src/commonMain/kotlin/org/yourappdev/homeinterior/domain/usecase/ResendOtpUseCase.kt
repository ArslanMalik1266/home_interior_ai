package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.VerifyResponse
import org.yourappdev.homeinterior.domain.repo.AuthRepository

class ResendOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String
    ): Result<VerifyResponse> {
        return repository.login(packageName, deviceId, userEmail, authProvider)
    }
}