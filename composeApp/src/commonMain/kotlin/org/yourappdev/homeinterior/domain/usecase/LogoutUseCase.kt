package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.DeviceLinkResult
import org.yourappdev.homeinterior.domain.model.LogoutDomainModel
import org.yourappdev.homeinterior.domain.repo.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        packageName: String,
        userEmail: String,
        deviceId: String,

    ): Result<LogoutDomainModel> {
        return repository.logout(packageName, userEmail,deviceId )
    }
}