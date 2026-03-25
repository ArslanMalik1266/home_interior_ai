package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.SpendCreditsResponse
import org.yourappdev.homeinterior.domain.repo.CreditsRepository

class SpendCreditsUseCase(
    private val creditsRepository: CreditsRepository
) {
    suspend operator fun invoke(
        userEmail: String,
        deviceId: String,
        amount: Int = 1,
        packageName: String = "org.yourappdev.homeinterior"
    ): Result<SpendCreditsResponse> {
        return creditsRepository.spendCredits(
            userEmail = userEmail,
            amount = amount,
            deviceId = deviceId,
            packageName = packageName
        )
    }
}

class SpendCreditsUseCaseGuest(
    private val creditsRepository: CreditsRepository
) {
    suspend operator fun invoke(
        deviceId: String,
        amount: Int = 1,
        packageName: String = "org.yourappdev.homeinterior"
    ): Result<SpendCreditsResponse> {
        return creditsRepository.spendCreditsGuest(
            amount = amount,
            deviceId = deviceId,
            packageName = packageName
        )
    }
}