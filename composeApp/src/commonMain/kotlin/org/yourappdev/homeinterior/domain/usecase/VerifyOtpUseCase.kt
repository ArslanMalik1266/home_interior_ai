package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.repo.AuthRepository

class VerifyOtpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        deviceId: String,
        userEmail: String,
        authProvider: String,
        otp: String
    ): Result<DeviceLinkResult> {
        return repository.verifyOtp(packageName, deviceId, userEmail, authProvider, otp)
    }
}