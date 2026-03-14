package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.repo.AuthRepository

class GetLoggedInUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): DeviceLinkResult? {

        return try {
            val result = authRepository.verifyOtp(
                packageName = "org.yourappdev.homeinterior",
                deviceId = "device-id-placeholder",
                userEmail = "user-email-placeholder",
                authProvider = "google",
                otp = "otp-placeholder"
            )
            if (result.isSuccess) result.getOrNull() else null
        } catch (e: Exception) {
            null
        }
    }
}